package com.github.jsontree.node;

import java.util.Date;

public class NumericNode extends AbstractNode implements LeafNode {

    private static final long serialVersionUID = -4314109649333671292L;

    private final Number value;

    public NumericNode(Number value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.number;
    }

    @Override
    public Boolean toBooleanValue() {
        return value.intValue() == 1;
    }

    @Override
    public Date toDateValue() {
        return new Date(value.longValue());
    }

    @Override
    public Number toNumber() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NumericNode other = (NumericNode) obj;
        if (value == null) {
            return other.value == null;
        } else return value.equals(other.value);
    }

    @Override
    public String toJson() {
        return value.toString();
    }

    @Override
    public String toString() {
        return value.toString();
    }


}
