package com.github.jsontree.node;

/**
 * @author liubo
 */
public class EmptyNode extends AbstractNode implements LeafNode {

    public static final EmptyNode MAP = new EmptyNode(Type.map);
    public static final EmptyNode LIST = new EmptyNode(Type.list);
    private static final long serialVersionUID = -3502318408147184859L;
    private final Type type;

    private EmptyNode(Type type) {
        this.type = type;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return type == Type.map ? "{}" : "[]";
    }

    @Override
    public String toJson() {
        return toString();
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        EmptyNode other = (EmptyNode) obj;
        return type == other.type;
    }

    @Override
    public String getStringValue(String defaultString) {
        return defaultString;
    }

}
