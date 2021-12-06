package com.cqx.common.utils.jdbc.declare;

/**
 * PGDeclare
 *
 * @author chenqixu
 */
public class PGDeclare extends AbstractDeclare {

    @Override
    protected String setPgdeclareInsertUpdate() {
        StringBuilder sb = new StringBuilder();
        sb.append("do")
                .append(" $$")
                .append(" DECLARE")
                .append("  hasval numeric;")
                .append(" BEGIN")
                .append(" %s;")
                .append(" if hasval=0 THEN")
                .append(" %s;")
                .append(" else")
                .append(" %s;")
                .append(" end if;")
                .append(" END;")
                .append(" $$");
        return sb.toString();
    }

    @Override
    protected String setPgdeclareInsertOnly() {
        StringBuilder sb = new StringBuilder();
        sb.append("do")
                .append(" $$")
                .append(" DECLARE")
                .append("  hasval numeric;")
                .append(" BEGIN")
                .append(" %s;")
                .append(" if hasval=0 THEN")
                .append(" %s;")
                .append(" end if;")
                .append(" END;")
                .append(" $$");
        return sb.toString();
    }
}
