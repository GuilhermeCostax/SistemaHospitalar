package br.com.hospital.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> naoEncontrado(NaoEncontradoException e) {
        return resposta(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, Object>> regraNegocio(RegraNegocioException e) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacao(MethodArgumentNotValidException e) {
        StringBuilder msg = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(erro ->
                msg.append(erro.getField()).append(": ").append(erro.getDefaultMessage()).append("; "));
        return resposta(HttpStatus.BAD_REQUEST, msg.toString());
    }

    // JSON malformado ou valor de enum invalido (ex: situacao "QUALQUER")
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> requisicaoInvalida(Exception e) {
        return resposta(HttpStatus.BAD_REQUEST, "Requisição inválida: verifique o formato dos dados e os valores permitidos.");
    }

    private ResponseEntity<Map<String, Object>> resposta(HttpStatus status, String mensagem) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", LocalDateTime.now().toString());
        corpo.put("status", status.value());
        corpo.put("erro", mensagem);
        return ResponseEntity.status(status).body(corpo);
    }
}
