package com.github.jsontree.node;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.jsontree.JacksonSupport;
import com.github.jsontree.Path;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


@JsonSerialize(using = JacksonSupport.ValueNodeSerializer.class)
@JsonDeserialize(using = JacksonSupport.ValueNodeDeserializer.class)
public interface ValueNode extends Iterable<ValueNode>, Serializable {

    /**
     * @return 判断是否为空 true 为空
     */
    boolean isEmpty();

    /**
     * @return 判断是否为空 true 为非空
     */
    boolean isNotEmpty();

    boolean isType(Type type);

    Type getType();

    List<ValueNode> find(String... path);

    List<ValueNode> find(int count, String... path);

    ValueNode firstChild(String... path);

    ValueNode get(int childIndex);

    int size();

    ValueNode get(String childName);

    Set<Entry<String, ValueNode>> entrySet();

    Set<String> keySet();

    Date toDateValue();

    Number toNumber();

    Boolean toBooleanValue();

    /**
     * 标准格式化的json
     *
     * @return
     */
    String toJson();

    /**
     * 标准json 去除空格制表符等压缩过的json
     *
     * @return
     */
    String toTrimJson();

    int getIntValue(int def);

    float getFloatValue(float def);

    long getLongValue(long def);

    String getStringValue(String defaultString);

    Map<Path, ValueNode> flatten();

    List<DiffValue> diff(ValueNode other);

    boolean contains(ValueNode other, Set<Path> ignores);

    enum Type {
        list, map, bool, number, string, NULL
    }

    interface DiffValue extends Comparable<DiffValue> {

        boolean getAction();

        Path getPath();

        ValueNode getValue();
    }
}
