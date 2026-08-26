package exception;

import java.sql.SQLException;

public class DatabaseException extends BusinessException {
    public DatabaseException(String message, Throwable cause) {
        super(montarMensagem(message, cause), cause);
    }

    private static String montarMensagem(String mensagemBase, Throwable cause) {
        if (!(cause instanceof SQLException sqlEx)) {
            return mensagemBase;
        }

        String sqlState = sqlEx.getSQLState();
        int codigo = sqlEx.getErrorCode();
        String detalhe = limparDetalhe(sqlEx.getMessage());

        String tipoErro = classificarPorSqlState(sqlState);
        return mensagemBase
                + " | Tipo: " + tipoErro
                + " | SQLState: " + (sqlState == null ? "N/A" : sqlState)
                + " | Código: " + codigo
                + (detalhe.isBlank() ? "" : " | Detalhe: " + detalhe);
    }

    private static String classificarPorSqlState(String sqlState) {
        if (sqlState == null || sqlState.isBlank()) {
            return "Erro de banco não classificado";
        }

        if (sqlState.startsWith("23")) {
            return "Violação de integridade (chave duplicada, relacionamento inválido ou restrição)";
        }
        if (sqlState.startsWith("08")) {
            return "Falha de conexão com o banco";
        }
        if (sqlState.startsWith("22")) {
            return "Valor de dado inválido";
        }
        if (sqlState.startsWith("42")) {
            return "Erro de sintaxe SQL ou permissão";
        }
        if ("40001".equals(sqlState)) {
            return "Transação interrompida (deadlock/concorrência)";
        }

        return "Erro SQL";
    }

    private static String limparDetalhe(String detalhe) {
        if (detalhe == null) {
            return "";
        }

        String normalizado = detalhe.replace('\n', ' ').replace('\r', ' ').trim();
        return normalizado.length() > 220 ? normalizado.substring(0, 220) + "..." : normalizado;
    }
}
