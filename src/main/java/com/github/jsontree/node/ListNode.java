package com.github.jsontree.node;


import com.github.jsontree.JacksonSupport;

import java.util.*;


public class ListNode extends AbstractNode {

    private static final long serialVersionUID = -1457476908248073127L;

    private final List<ValueNode> list = new ArrayList<ValueNode>();


    public ListNode() {
        super();
    }

    @Override
    public ValueNode.Type getType() {
        return ValueNode.Type.list;
    }


    @Override
    public boolean isEmpty() {
        return super.isEmpty() || list.isEmpty();
    }

    public void add(ValueNode node) {
        list.add(node);
    }

    public ListNode addAll(Collection<? extends ValueNode> nodes) {
        list.addAll(nodes);
        return this;
    }

    @Override
    public Iterator<ValueNode> iterator() {
        return list.iterator();
    }

    @Override
    public ValueNode get(int childIndex) {
        return list.get(childIndex);
    }

    public List<ValueNode> getAll() {
        return Collections.unmodifiableList(list);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public String toJson() {
        return JacksonSupport.toJson(this);
    }

    @Override
    public String toTrimJson() {
        return JacksonSupport.toTrimjson(this);
    }

}
