package com.fiap.ec.urbanrisk.exception;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Tratamento global de erros: o cliente sempre recebe {"erro": "mensagem"}
 * com o status HTTP correto, e nunca um stack trace.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleNaoEncontrado(RecursoNaoEncontradoException ex) {
        return erro(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<Map<String, String>> handleInvalido(DadosInvalidosException ex) {
        return erro(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidacao(MethodArgumentNotValidException ex) {
        FieldError campo = ex.getBindingResult().getFieldError();
        String mensagem = campo != null ? campo.getDefaultMessage() : "Dados inválidos.";
        return erro(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleCorpoInvalido(HttpMessageNotReadableException ex) {
        return erro(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido. Confira os campos e os valores enviados.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleParametro(MethodArgumentTypeMismatchException ex) {
        return erro(HttpStatus.BAD_REQUEST, "Valor inválido para o parâmetro '" + ex.getName() + "'.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleRotaInexistente(NoResourceFoundException ex) {
        return erro(HttpStatus.NOT_FOUND, "Recurso não encontrado.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMetodo(HttpRequestMethodNotSupportedException ex) {
        return erro(HttpStatus.METHOD_NOT_ALLOWED, "Método não permitido para este recurso.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenerico(Exception ex) {
        log.error("Erro inesperado", ex);
        return erro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno. Tente novamente em instantes.");
    }

    private ResponseEntity<Map<String, String>> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("erro", mensagem));
    }
}
