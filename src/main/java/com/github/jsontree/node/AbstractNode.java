package com.github.jsontree.node;


import com.github.jsontree.Path;
import com.github.jsontree.PathFormat;

import java.util.*;
import java.util.Map.Entry;


/**
 * @author liubo
 */
public abstract class AbstractNode implements ValueNode {

    private static final long serialVersionUID = -5030207966278794611L;
    private static final PathFormat parser = new PathFormat();


    @Override
    public boolean isType(Type type) {
        assert type != null;
        return getType().equals(type);
    }

    @Override
    public boolean isEmpty() {
        return this.getType() == null || this.getType().equals(ValueNode.Type.NULL);
    }

    @Override
    public boolean isNotEmpty() {
        return !isEmpty();
    }

    @Override
    public Iterator<ValueNode> iterator() {
        List<ValueNode> empty = Collections.emptyList();
        return empty.iterator();
    }

    @Override
    public ValueNode firstChild(String... paths) {
        List<ValueNode> list = find(1, paths);
        return list.size() > 0 ? list.get(0) : NullNode.getRoot();
    }

    @Override
    public List<ValueNode> find(String... paths) {
        return find(0, paths);
    }

    @Override
    public List<ValueNode> find(int count, String... paths) {
        if (paths.length == 0) {
            throw new IllegalArgumentException("paths must defined ");
        }

        if (paths.length == 1 && paths[0].indexOf('.') != -1) {

            List<String> l = parser.parseLine(paths[0]);
            paths = new String[l.size()];
            l.toArray(paths);
        }

        List<ValueNode> list = new ArrayList<>(count == 0 ? 16 : count);
        find(count == 0 ? Short.MAX_VALUE : count, this, list, paths, 0);
        return list;
    }

    private void find(int count, ValueNode node, List<ValueNode> list, String[] paths, int depth) {
        if (list.size() >= count || node.getType() == Type.NULL) {
            return;
        }
        Type type = node.getType();

        if (type == Type.map) {
            findMap(count, (MapNode) node, list, paths, depth);
        } else if (type == Type.list) {
            findList(count, (ListNode) node, list, paths, depth);
        }
    }

    private void findList(int count, ListNode node, List<ValueNode> list, String[] paths, int depth) {

        int nextDepth = depth + 1;
        boolean end = nextDepth == paths.length;
        String key = paths[depth];

        List<ValueNode> selected;

        if (key == null || "".equals(key) || "*".equals(key)) {
            selected = node.getAll();
        } else {
            int index;
            try {
                index = Integer.parseInt(key);
            } catch (NumberFormatException e) {
                return;
            }

            if (index >= node.size()) {
                return;
            }
            selected = Collections.singletonList(node.get(index));
        }


        for (ValueNode vn : selected) {
            if (end) {
                list.add(vn);
                if (list.size() >= count) {
                    return;
                }
            } else {
                find(count, vn, list, paths, nextDepth);
            }
        }

    }

    private void findMap(int count, MapNode node, List<ValueNode> list, String[] paths, int depth) {

        int nextDepth = depth + 1;
        boolean end = nextDepth == paths.length;
        String key = paths[depth];

        Collection<String> keys;

        if (key == null || key.equals("") || key.equals("*")) {
            keys = node.keySet();
        } else {
            keys = Arrays.asList(key);
        }

        for (String k : keys) {
            ValueNode vn = node.get(k);
            if (vn.getType() == Type.NULL) {
                continue;
            }

            if (end) {
                list.add(vn);
                if (list.size() >= count) {
                    return;
                }
            } else {
                find(count, vn, list, paths, nextDepth);
            }
        }

    }

    private void walk(ValueNode node, Map<Path, ValueNode> map, Path path) {
        Type type = node.getType();
        switch (type) {
            case map:
                map.put(path, EmptyNode.MAP);
                for (Entry<String, ValueNode> entry : node.entrySet()) {
                    walk(entry.getValue(), map, path.append(entry.getKey()));
                }
                break;
            case list:
                map.put(path, EmptyNode.LIST);
                int i = 0;
                for (ValueNode entry : node) {
                    walk(entry, map, path.append((i++) + ""));
                }
                break;
            default:
                map.put(path, node);
        }
    }

    @Override
    public Map<Path, ValueNode> flatten() {
        LinkedHashMap<Path, ValueNode> map = new LinkedHashMap<>();
        walk(this, map, Path.GOD);
        return map;
    }

    @Override
    public boolean contains(ValueNode other, Set<Path> ignores) {
        Map<Path, ValueNode> t = this.flatten();
        Map<Path, ValueNode> o = other.flatten();

        // 对于ignores里的就不用比较了
        if (ignores != null && !ignores.isEmpty()) {
            for (Path ignore : ignores) {
                t.remove(ignore);
                o.remove(ignore);
            }
        }

        for (Entry<Path, ValueNode> entry : t.entrySet()) {
            Path path = entry.getKey();
            ValueNode tNode = entry.getValue();
            ValueNode oNode = o.remove(path);
            if (!tNode.equals(oNode)) {
                return false;
            }
        }

        return o.size() <= 0;
    }

    @Override
    public List<DiffValue> diff(ValueNode other) {
        Map<Path, ValueNode> t = this.flatten();
        Map<Path, ValueNode> o = other.flatten();

        ArrayList<DiffValue> diffs = new ArrayList<>();

        for (Entry<Path, ValueNode> entry : t.entrySet()) {
            Path path = entry.getKey();
            ValueNode tNode = entry.getValue();
            ValueNode oNode = o.remove(path);

            if (!tNode.equals(oNode)) {
                if (oNode != null) {
                    diffs.add(new DiffNode(false, path, oNode));
                }
                diffs.add(new DiffNode(true, path, tNode));
            }
        }

        for (Entry<Path, ValueNode> entry : o.entrySet()) {
            diffs.add(new DiffNode(false, entry.getKey(), entry.getValue()));
        }

        Collections.sort(diffs);
        return diffs;

    }

    @Override
    public ValueNode get(int childIndex) {
        return NullNode.getRoot();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public ValueNode get(String childName) {
        return NullNode.getRoot();
    }

    @Override
    public Set<Entry<String, ValueNode>> entrySet() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> keySet() {
        return Collections.emptySet();
    }

    @Override
    public Boolean toBooleanValue() {
        return false;
    }

    @Override
    public Number toNumber() {
        return null;
    }

    @Override
    public Date toDateValue() {
        return null;
    }

    @Override
    public int getIntValue(int def) {
        Number num = toNumber();
        return num == null ? def : num.intValue();
    }

    @Override
    public long getLongValue(long def) {
        Number num = toNumber();
        return num == null ? def : num.longValue();
    }

    @Override
    public float getFloatValue(float def) {
        Number num = toNumber();
        return num == null ? def : num.floatValue();
    }

    @Override
    public String getStringValue(String defaultString) {
        return this.toString();
    }

    @Override
    public String toTrimJson() {
        return toJson();
    }

    private static class DiffNode implements DiffValue {

        final boolean action;
        final Path path;
        final ValueNode value;

        DiffNode(boolean action, Path path, ValueNode value) {
            super();
            this.action = action;
            this.path = path;
            this.value = value;
        }

        @Override
        public boolean getAction() {
            return action;
        }

        @Override
        public Path getPath() {
            return path;
        }

        @Override
        public ValueNode getValue() {
            return value;
        }

        @Override
        public int compareTo(DiffValue other) {
            DiffNode o = (DiffNode) other;
            int i = path.compareTo(o.path);
            if (i != 0) {
                return i;
            }
            if (this.action == o.action) {
                return 0;
            }
            return this.action ? 1 : -1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            DiffNode diffNode = (DiffNode) o;
            return action == diffNode.action &&
                    Objects.equals(path, diffNode.path) &&
                    Objects.equals(value, diffNode.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(action, path, value);
        }

        @Override
        public String toString() {
            return (action ? '+' : '-') + " " + path + "=" + value.toJson();
        }

    }
}
