package com.fiap.ec.urbanrisk.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.fiap.ec.urbanrisk.model.NivelAlerta;

/**
 * Recurso de análise da plataforma.
 *
 * 1. Score de prioridade (0 a 100): combinação ponderada de severidade, confiança,
 *    impacto (pessoas na região), recência e corroboração entre origens.
 * 2. Detecção de anomalia nas leituras por z-score sobre a média móvel do sensor.
 *
 * Java puro, sem banco nem Spring além da anotação, para ser testável isoladamente.
 * Pesos e limites estão explicados no README.
 */
@Service
public class PriorizacaoService {

    // Pesos do score (somam 1)
    public static final double PESO_SEVERIDADE = 0.35;
    public static final double PESO_CONFIANCA = 0.25;
    public static final double PESO_IMPACTO = 0.15;
    public static final double PESO_RECENCIA = 0.15;
    public static final double PESO_CORROBORACAO = 0.10;

    // Região com 500 mil moradores ou mais tem impacto máximo
    public static final double POPULACAO_REFERENCIA = 500_000;

    // A recência cai pela metade a cada 3 horas sem nova evidência
    public static final double MEIA_VIDA_HORAS = 3.0;

    // Janela em que outra origem conta como corroboração
    public static final Duration JANELA_CORROBORACAO = Duration.ofHours(2);

    // Limites dos níveis de alerta
    public static final double LIMITE_CRITICO = 75;
    public static final double LIMITE_ALTO = 55;
    public static final double LIMITE_MODERADO = 35;

    // Detecção de anomalia
    public static final double Z_LIMITE = 2.5;
    public static final int JANELA_MEDIA_MOVEL = 20;
    public static final int MINIMO_AMOSTRAS = 5;

    private static final Locale PT_BR = Locale.of("pt", "BR");

    /**
     * Fatores que entram no score.
     *
     * @param severidade          0 a 1
     * @param confianca           0 a 100
     * @param populacaoRegiao     moradores estimados na região
     * @param idade               tempo desde a última evidência
     * @param origensCorroborando quantas origens diferentes (sensor, base pública, relato)
     *                            apontam o mesmo risco na mesma região, incluindo esta
     * @param descricaoSeveridade texto curto do que foi medido (ex.: "Chuva de 48 mm/h")
     */
    public record Fatores(double severidade, int confianca, int populacaoRegiao, Duration idade,
            int origensCorroborando, String descricaoSeveridade) {
    }

    public record Resultado(double score, NivelAlerta nivel, String motivo) {
    }

    public Resultado calcular(Fatores f) {
        double severidade = limitar(f.severidade());
        double confianca = limitar(f.confianca() / 100.0);
        double impacto = impacto(f.populacaoRegiao());
        double recencia = recencia(f.idade());
        double corroboracao = corroboracao(f.origensCorroborando());

        double score = 100 * (PESO_SEVERIDADE * severidade
                + PESO_CONFIANCA * confianca
                + PESO_IMPACTO * impacto
                + PESO_RECENCIA * recencia
                + PESO_CORROBORACAO * corroboracao);
        score = Math.round(score * 10) / 10.0;

        return new Resultado(score, nivel(score), motivo(f, impacto, recencia));
    }

    public NivelAlerta nivel(double score) {
        if (score >= LIMITE_CRITICO) {
            return NivelAlerta.CRITICO;
        }
        if (score >= LIMITE_ALTO) {
            return NivelAlerta.ALTO;
        }
        if (score >= LIMITE_MODERADO) {
            return NivelAlerta.MODERADO;
        }
        return NivelAlerta.BAIXO;
    }

    public double impacto(int populacao) {
        return limitar(populacao / POPULACAO_REFERENCIA);
    }

    /** 1 para evidência agora, 0,5 depois de 3 h, 0,25 depois de 6 h... */
    public double recencia(Duration idade) {
        double horas = Math.max(0, idade.toSeconds()) / 3600.0;
        return Math.pow(0.5, horas / MEIA_VIDA_HORAS);
    }

    /** 1 origem = 0; 2 origens = 0,6; 3 origens = 1. */
    public double corroboracao(int origens) {
        if (origens >= 3) {
            return 1.0;
        }
        if (origens == 2) {
            return 0.6;
        }
        return 0.0;
    }

    /**
     * z = (valor - média) / desvio padrão, sobre as últimas leituras do mesmo sensor.
     * Com menos de 5 amostras devolve 0 (ainda não há base para comparar).
     */
    public double zScore(List<Double> historico, double valor) {
        if (historico == null || historico.size() < MINIMO_AMOSTRAS) {
            return 0;
        }
        double media = media(historico);
        double soma = 0;
        for (double v : historico) {
            soma += (v - media) * (v - media);
        }
        double desvio = Math.sqrt(soma / (historico.size() - 1));
        // Piso no desvio: evita z enorme quando a série é quase constante
        double piso = Math.max(0.01, Math.abs(media) * 0.02);
        desvio = Math.max(desvio, piso);
        return (valor - media) / desvio;
    }

    public boolean isAnomalia(double zScore) {
        return Math.abs(zScore) > Z_LIMITE;
    }

    public double media(List<Double> valores) {
        if (valores == null || valores.isEmpty()) {
            return 0;
        }
        double soma = 0;
        for (double v : valores) {
            soma += v;
        }
        return soma / valores.size();
    }

    private String motivo(Fatores f, double impacto, double recencia) {
        List<String> partes = new ArrayList<>();
        if (f.descricaoSeveridade() != null && !f.descricaoSeveridade().isBlank()) {
            partes.add(f.descricaoSeveridade());
        }
        if (f.origensCorroborando() >= 2) {
            partes.add(f.origensCorroborando() + " origens confirmam");
        }
        partes.add("confiança " + f.confianca() + "%");
        if (impacto >= 0.8) {
            partes.add(String.format(PT_BR, "região com ~%d mil moradores", Math.round(f.populacaoRegiao() / 1000.0)));
        }
        if (recencia < 0.5) {
            partes.add("sem novidade há " + Math.max(1, f.idade().toHours()) + " h");
        }
        return String.join(" + ", partes);
    }

    private static double limitar(double v) {
        return Math.max(0, Math.min(1, v));
    }
}
