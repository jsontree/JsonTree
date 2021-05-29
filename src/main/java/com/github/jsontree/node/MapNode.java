package com.github.jsontree.node;


import com.github.jsontree.JacksonSupport;
import com.github.jsontree.util.ValueNodes;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class MapNode extends AbstractNode {

    private static final long serialVersionUID = 697816776137475507L;

    LinkedHashMap<String, ValueNode> map = new LinkedHashMap<String, ValueNode>();

    /**
     *
     */
    public MapNode() {
        super();
    }


    public MapNode(String keys, ValueNode value) {
        super();
        put(keys, value);
    }


    public MapNode(String keys, String value) {
        super();
        put(keys, new StringNode(value));
    }

    public MapNode(String keys) {
        super();
        put(keys, NullNode.getRoot());
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() || map.isEmpty();
    }

    @Override
    public Type getType() {
        return Type.map;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public ValueNode get(String childName) {
        ValueNode node = map.get(childName);
        return node == null ? NullNode.getRoot() : node;
    }

    @Override
    public Set<Entry<String, ValueNode>> entrySet() {
        return Collections.unmodifiableSet(map.entrySet());
    }

    public Map<String, ValueNode> getMap() {
        return Collections.unmodifiableMap(map);
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    public int getIntValue(String key, int def) {
        return map.get(key).getIntValue(def);
    }

    public long getLongValue(String key, long def) {
        return map.get(key).getLongValue(def);
    }

    public ValueNode set(String key, String value) {
        return map.put(key, new StringNode(value));
    }

    public ValueNode set(String key, float value) {
        return map.put(key, new NumericNode(value));
    }

    public ValueNode set(String key, int value) {
        return map.put(key, new NumericNode(value));
    }

    public ValueNode set(String key, long value) {
        return map.put(key, new NumericNode(value));
    }

    public ValueNode set(String key, ValueNode value) {
        return map.put(key, value);
    }

    public ValueNode set(String key, boolean value) {
        return map.put(key, new BooleanValue(value));
    }

    public ValueNode remove(String key) {
        return map.remove(key);
    }

    public MapNode putAll(Map<? extends String, ? extends ValueNode> items) {
        if (items != null)
            map.putAll(items);
        return this;
    }

    @Override
    public String toJson() {
        return JacksonSupport.toJson(this);
    }


    public MapNode put(String keys, ValueNode value) {
        int pos = keys.indexOf(".");
        if (pos > -1) {
            String key = keys.substring(0, pos);
            ValueNode keyNode = this.get(key);
            String subKeys = keys.substring(pos + 1);
            if (ValueNodes.isNotEmpty(keyNode) && keyNode.isType(Type.map)) {
                ((MapNode) keyNode).put(subKeys, value);
            } else {
                this.put(key, new MapNode(subKeys, value));
            }
        } else {
            this.set(keys, value);
        }
        return this;
    }


    public MapNode put(String keys, String value) {
        if (value == null) {
            return put(keys, NullNode.getRoot());
        }
        return put(keys, new StringNode(value));
    }

    public MapNode put(String keys, int value) {
        return put(keys, ValueNodes.toNode(value));
    }

    public MapNode put(String keys, long value) {
        return put(keys, ValueNodes.toNode(value));
    }

    public MapNode put(String keys, Enum<?> value) {
        return put(keys, ValueNodes.toNode(value));
    }


    @Override
    public String toTrimJson() {
        return JacksonSupport.toTrimjson(this);
    }

}
