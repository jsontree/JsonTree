package com.github.jsontree.node;


import com.github.jsontree.JacksonSupport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;


public class StringNode extends AbstractNode implements LeafNode {

    private static final long serialVersionUID = -606620138622554905L;

    private static final String EMPTY = "";

    private String text;

    public StringNode(String text) {
        this.text = text;
    }

    public void setValue(String value) {
        this.text = value;
    }

    @Override
    public Type getType() {
        return Type.string;
    }

    @Override
    public Boolean toBooleanValue() {
        return "1".equals(text) || "true".equalsIgnoreCase(text);
    }

    @Override
    public Date toDateValue() {
        return Timestamp.valueOf(text);
    }

    @Override
    public Number toNumber() {
        try {
            return new BigDecimal(text);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StringNode other = (StringNode) obj;
        if (text == null) {
            return other.text == null;
        } else {
            return text.equals(other.text);
        }
    }

    @Override
    public String toJson() {
        return JacksonSupport.toJson(text);
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() || this.text.equals(EMPTY);
    }

}
