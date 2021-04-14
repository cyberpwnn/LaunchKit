package ninja.bytecode.shuriken.io.bytetag.tag;

import ninja.bytecode.shuriken.collections.KMap;
import ninja.bytecode.shuriken.io.bytetag.NBTRawMaxDepthIO;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

public class NBTCompoundTag extends NBTTag<Map<String, NBTTag<?>>> implements Iterable<Map.Entry<String, NBTTag<?>>>, Comparable<NBTCompoundTag>, NBTRawMaxDepthIO {

	public static final byte ID = 10;

	public NBTCompoundTag() {
		this(createEmptyValue());
	}

	public NBTCompoundTag(Map<String, NBTTag<?>> map) {
		super(map);
	}

	@Override
	public byte getID() {
		return ID;
	}

	private static Map<String, NBTTag<?>> createEmptyValue() {
		return new KMap<>(8);
	}

	public int size() {
		return getValue().size();
	}

	public NBTTag<?> remove(String key) {
		return getValue().remove(key);
	}

	public void clear() {
		getValue().clear();
	}

	public boolean containsKey(String key) {
		return getValue().containsKey(key);
	}

	public boolean containsValue(NBTTag<?> value) {
		return getValue().containsValue(value);
	}

	public Collection<NBTTag<?>> values() {
		return getValue().values();
	}

	public Set<String> keySet() {
		return getValue().keySet();
	}

	public Set<Map.Entry<String, NBTTag<?>>> entrySet() {
		return new NBTNonNullEntrySet<>(getValue().entrySet());
	}

	@Override
	public Iterator<Map.Entry<String, NBTTag<?>>> iterator() {
		return entrySet().iterator();
	}

	public void forEach(BiConsumer<String, NBTTag<?>> action) {
		getValue().forEach(action);
	}

	public <C extends NBTTag<?>> C get(String key, Class<C> type) {
		NBTTag<?> t = getValue().get(key);
		if (t != null) {
			return type.cast(t);
		}
		return null;
	}

	public NBTTag<?> get(String key) {
		return getValue().get(key);
	}

	public NBTByteTag getByteTag(String key) {
		return get(key, NBTByteTag.class);
	}

	public NBTShortTag getShortTag(String key) {
		return get(key, NBTShortTag.class);
	}

	public NBTIntTag getIntTag(String key) {
		return get(key, NBTIntTag.class);
	}

	public NBTLongTag getLongTag(String key) {
		return get(key, NBTLongTag.class);
	}

	public NBTFloatTag getFloatTag(String key) {
		return get(key, NBTFloatTag.class);
	}

	public NBTDoubleTag getDoubleTag(String key) {
		return get(key, NBTDoubleTag.class);
	}

	public NBTStringTag getStringTag(String key) {
		return get(key, NBTStringTag.class);
	}

	public NBTByteArrayTag getByteArrayTag(String key) {
		return get(key, NBTByteArrayTag.class);
	}

	public NBTIntArrayTag getIntArrayTag(String key) {
		return get(key, NBTIntArrayTag.class);
	}

	public NBTLongArrayTag getLongArrayTag(String key) {
		return get(key, NBTLongArrayTag.class);
	}

	public NBTListTag<?> getListTag(String key) {
		return get(key, NBTListTag.class);
	}

	public NBTCompoundTag getCompoundTag(String key) {
		return get(key, NBTCompoundTag.class);
	}

	public boolean getBoolean(String key) {
		NBTTag<?> t = get(key);
		return t instanceof NBTByteTag && ((NBTByteTag) t).asByte() > 0;
	}

	public byte getByte(String key) {
		NBTByteTag t = getByteTag(key);
		return t == null ? NBTByteTag.ZERO_VALUE : t.asByte();
	}

	public short getShort(String key) {
		NBTShortTag t = getShortTag(key);
		return t == null ? NBTShortTag.ZERO_VALUE : t.asShort();
	}

	public int getInt(String key) {
		NBTIntTag t = getIntTag(key);
		return t == null ? NBTIntTag.ZERO_VALUE : t.asInt();
	}

	public long getLong(String key) {
		NBTLongTag t = getLongTag(key);
		return t == null ? NBTLongTag.ZERO_VALUE : t.asLong();
	}

	public float getFloat(String key) {
		NBTFloatTag t = getFloatTag(key);
		return t == null ? NBTFloatTag.ZERO_VALUE : t.asFloat();
	}

	public double getDouble(String key) {
		NBTDoubleTag t = getDoubleTag(key);
		return t == null ? NBTDoubleTag.ZERO_VALUE : t.asDouble();
	}

	public String getString(String key) {
		NBTStringTag t = getStringTag(key);
		return t == null ? NBTStringTag.ZERO_VALUE : t.getValue();
	}

	public byte[] getByteArray(String key) {
		NBTByteArrayTag t = getByteArrayTag(key);
		return t == null ? NBTByteArrayTag.ZERO_VALUE : t.getValue();
	}

	public int[] getIntArray(String key) {
		NBTIntArrayTag t = getIntArrayTag(key);
		return t == null ? NBTIntArrayTag.ZERO_VALUE : t.getValue();
	}

	public long[] getLongArray(String key) {
		NBTLongArrayTag t = getLongArrayTag(key);
		return t == null ? NBTLongArrayTag.ZERO_VALUE : t.getValue();
	}

	public NBTTag<?> put(String key, NBTTag<?> tag) {
		return getValue().put(Objects.requireNonNull(key), Objects.requireNonNull(tag));
	}

	public NBTTag<?> putBoolean(String key, boolean value) {
		return put(key, new NBTByteTag(value));
	}

	public NBTTag<?> putByte(String key, byte value) {
		return put(key, new NBTByteTag(value));
	}

	public NBTTag<?> putShort(String key, short value) {
		return put(key, new NBTShortTag(value));
	}

	public NBTTag<?> putInt(String key, int value) {
		return put(key, new NBTIntTag(value));
	}

	public NBTTag<?> putLong(String key, long value) {
		return put(key, new NBTLongTag(value));
	}

	public NBTTag<?> putFloat(String key, float value) {
		return put(key, new NBTFloatTag(value));
	}

	public NBTTag<?> putDouble(String key, double value) {
		return put(key, new NBTDoubleTag(value));
	}

	public NBTTag<?> putString(String key, String value) {
		return put(key, new NBTStringTag(value));
	}

	public NBTTag<?> putByteArray(String key, byte[] value) {
		return put(key, new NBTByteArrayTag(value));
	}

	public NBTTag<?> putIntArray(String key, int[] value) {
		return put(key, new NBTIntArrayTag(value));
	}

	public NBTTag<?> putLongArray(String key, long[] value) {
		return put(key, new NBTLongArrayTag(value));
	}

	@Override
	public String valueToString(int maxDepth) {
		StringBuilder sb = new StringBuilder("{");
		boolean first = true;
		for (Map.Entry<String, NBTTag<?>> e : getValue().entrySet()) {
			sb.append(first ? "" : ",")
					.append(escapeString(e.getKey(), false)).append(":")
					.append(e.getValue().toString(decrementMaxDepth(maxDepth)));
			first = false;
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!super.equals(other) || size() != ((NBTCompoundTag) other).size()) {
			return false;
		}
		for (Map.Entry<String, NBTTag<?>> e : getValue().entrySet()) {
			NBTTag<?> v;
			if ((v = ((NBTCompoundTag) other).get(e.getKey())) == null || !e.getValue().equals(v)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int compareTo(NBTCompoundTag o) {
		return Integer.compare(size(), o.getValue().size());
	}

	@Override
	public NBTCompoundTag clone() {
		NBTCompoundTag copy = new NBTCompoundTag();
		for (Map.Entry<String, NBTTag<?>> e : getValue().entrySet()) {
			copy.put(e.getKey(), e.getValue().clone());
		}
		return copy;
	}

	public Map<String, NBTTag<?>> getMap() {
		return getValue();
	}
}
