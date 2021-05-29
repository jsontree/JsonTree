package com.github.jsontree.node;

public final class NullNode extends AbstractNode implements LeafNode {

    static final NullNode[] nodes = new NullNode[100];
    private static final long serialVersionUID = -363616870094121511L;

    static {
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new NullNode(i);
        }
    }

    private final int depth;


    private NullNode(int depth) {
        this.depth = depth;
    }

    public static NullNode getRoot() {
        return nodes[0];
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Type getType() {
        return Type.NULL;
    }

    @Override
    public ValueNode get(int childIndex) {
        return getNext();
    }

    @Override
    public ValueNode get(String childName) {
        return getNext();
    }

    private ValueNode getNext() {
        int i = depth + 1;
        if (i >= nodes.length) {
            throw new RuntimeException("递归深度过大, 可能出现死循环.");
        }
        return nodes[i];
    }

    @Override
    public int hashCode() {
        return 32541;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        return getClass() == obj.getClass();
    }

    @Override
    public String toJson() {
        return "null";
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public String getStringValue(String defaultString) {
        return defaultString;
    }
}
