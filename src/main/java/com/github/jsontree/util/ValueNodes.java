package com.github.jsontree.util;


import com.github.jsontree.JacksonSupport;
import com.github.jsontree.PathFormat;
import com.github.jsontree.exception.ServiceException;
import com.github.jsontree.node.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ValueNodes {

    private static final PathFormat PATH_FORMAT = new PathFormat();


    public static ValueNode addNode(ValueNode parentNode, String path, ValueNode childNode) {
        List<String> paths = PATH_FORMAT.parseLine(path);
        if (paths.size() == 0) {
            throw new IllegalArgumentException("path 路径错误");
        }
        ValueNode currentNode = parentNode;
        for (int i = 0; i < paths.size() - 1; i++) {
            String currentChildPath = paths.get(i);
            ValueNode tempChildNode = currentNode.get(currentChildPath);
            if (tempChildNode == null || tempChildNode instanceof NullNode || tempChildNode instanceof EmptyNode) {
                tempChildNode = new MapNode();
                MapNode mapCurrentNode = (MapNode) currentNode;
                mapCurrentNode.set(currentChildPath, tempChildNode);
            }
            if (!(tempChildNode instanceof MapNode)) {
                throw new IllegalArgumentException("error ValueNode Type path"
                        + path.substring(0, path.indexOf(currentChildPath) + currentChildPath.length()) + " type:"
                        + tempChildNode.getClass());
            }
            currentNode = tempChildNode;
        }
        MapNode lastParentNode = (MapNode) currentNode;
        lastParentNode.set(paths.get(paths.size() - 1), childNode);

        return parentNode;
    }

    public static ValueNode partValueNodes(ValueNode originalValueNode, String... paths) {
        ValueNode partNode = new MapNode();
        if (paths != null) {
            for (String path : paths) {
                addNode(partNode, path, originalValueNode.firstChild(path));
            }
            return partNode;
        } else {
            return originalValueNode;
        }

    }

    public static ValueNode partValueNodes(ValueNode originalValueNode, List<String> paths) {
        ValueNode partNode = new MapNode();
        if (paths != null) {
            for (String path : paths) {
                addNode(partNode, path, originalValueNode.firstChild(path));
            }
            return partNode;
        } else {
            return originalValueNode;
        }
    }

    public static boolean isEmpty(ValueNode valueNode) {
        return valueNode == null || valueNode.isEmpty();
    }

    public static boolean isNotEmpty(ValueNode valueNode) {
        return !isEmpty(valueNode);
    }


    public static List<MapNode> toMapNodes(ValueNode valueNode) throws ServiceException {
        if (isEmpty(valueNode)) {
            return Collections.unmodifiableList(new ArrayList<MapNode>());
        }
        if (valueNode.getType().equals(ValueNode.Type.map)) {
            List<MapNode> list = new ArrayList<>();
            list.add((MapNode) valueNode);
            return Collections.unmodifiableList(list);
        }
        if (valueNode.getType().equals(ValueNode.Type.list)) {
            List<MapNode> result = new ArrayList<>();
            for (ValueNode valueNode2 : valueNode) {
                if (valueNode2.getType().equals(ValueNode.Type.map)) {
                    result.add((MapNode) valueNode2);
                }
            }
            return result;
        }

        throw new ServiceException("valueNode type is not mapNode type:" + valueNode.getType().name());
    }

    public static List<ValueNode> toListNodes(ValueNode valueNode) {
        if (isEmpty(valueNode)) {
            return Collections.unmodifiableList(new ArrayList<ValueNode>());
        }
        if (valueNode.getType().equals(ValueNode.Type.list)) {
            List<ValueNode> result = new ArrayList<>();
            for (ValueNode valueNode2 : valueNode) {
                result.add(valueNode2);
            }
            return result;
        }
        List<ValueNode> list = new ArrayList<>();
        list.add(valueNode);
        return Collections.unmodifiableList(list);
    }


    public static ValueNode parse(Class<?> pathClass, String filePath) {
        return JacksonSupport.parse(new File(Objects.requireNonNull(pathClass.getResource(filePath)).getPath()));
    }

    public static ValueNode toNode(Integer value) {
        if (value == null) {
            return NullNode.getRoot();
        }
        return new NumericNode(value);
    }

    public static ValueNode toNode(Long value) {
        if (value == null) {
            return NullNode.getRoot();
        }
        return new NumericNode(value);
    }

    public static ValueNode toNode(String value) {
        if (value == null) {
            return NullNode.getRoot();
        }
        return new StringNode(value);
    }

    public static ValueNode toNode(Enum<?> value) {
        if (value == null) {
            return NullNode.getRoot();
        }
        return new StringNode(value.toString());
    }

    public static void removeSubNodeByPaths(ValueNode orderNode, List<String> paths) {
        if (isEmpty(orderNode)) {
            return;
        }
        if (paths == null || paths.size() == 0) {
            return;
        }
        for (String path : paths) {
            removeSubNodeByPath(orderNode, path);
        }
    }

    /**
     * 删除节点中指定路径字段，暂不支持list节点和list路径
     *
     * @param orderNode 要删除的node
     * @param path      删除字段路径 例如product.common.price
     */
    public static void removeSubNodeByPath(ValueNode orderNode, String path) {
        if (path == null || "".equals(path)) {
            return;
        }
        List<String> pathFields = PATH_FORMAT.parseLine(path);
        if (pathFields.size() == 0) {
            throw new IllegalArgumentException("path 路径错误");
        }
        if (!orderNode.isType(ValueNode.Type.map)) {
            throw new IllegalArgumentException("暂时只支持map类型节点");
        }
        if (pathFields.size() == 1) {
            if (orderNode.isType(ValueNode.Type.map)) {
                ((MapNode) orderNode).remove(pathFields.get(0));
            }
            return;
        }
        String keyToRemove = pathFields.get(pathFields.size() - 1);
        String parentPath = path.substring(0, path.lastIndexOf(keyToRemove) - 1);
        List<ValueNode> parentNodeOnPaths = orderNode.find(parentPath);
        for (ValueNode parentNodeOnPath : parentNodeOnPaths) {
            if (parentNodeOnPath.isType(ValueNode.Type.map)) {
                ((MapNode) parentNodeOnPath).remove(keyToRemove);
            }
        }
    }


    public static boolean isType(ValueNode ancestorNode, ValueNode.Type type) {
        if (ancestorNode == null) {
            return false;
        }
        assert type != null;
        return ancestorNode.isType(type);
    }

}
