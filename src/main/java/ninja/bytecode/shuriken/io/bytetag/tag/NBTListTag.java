package ninja.bytecode.shuriken.io.bytetag.tag;

import ninja.bytecode.shuriken.io.bytetag.NBTRawMaxDepthIO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * ListTag represents a typed List in the nbt structure.
 * An empty {@link NBTListTag} created using {@link NBTListTag#createUnchecked(Class)} will be of unknown type
 * and returns an {@link NBTEndTag}{@code .class} in {@link NBTListTag#getTypeClass()}.
 * The type of an empty untyped {@link NBTListTag} can be set by using any of the {@code add()}
 * methods or any of the {@code as...List()} methods.
 * */
public class NBTListTag<T extends NBTTag<?>> extends NBTTag<List<T>> implements Iterable<T>, Comparable<NBTListTag<T>>, NBTRawMaxDepthIO {

	public static final byte ID = 9;

	private Class<?> typeClass = null;

	private NBTListTag() {
		super(createEmptyValue(3));
	}

	@Override
	public byte getID() {
		return ID;
	}
	
	/**
	 * <p>Creates a non-type-safe ListTag. Its element type will be set after the first 
	 * element was added.</p>
	 * 
	 * <p>This is an internal helper method for cases where the element type is not known 
	 * at construction time. Use {@link #NBTListTag(Class)} when the type is known.</p>
	 * 
	 * @return A new non-type-safe ListTag
	 */
	public static NBTListTag<?> createUnchecked(Class<?> typeClass) {
		NBTListTag<?> list = new NBTListTag<>();
		list.typeClass = typeClass;
		return list;
	}

	/**
	 * <p>Creates an empty mutable list to be used as empty value of ListTags.</p>
	 *
	 * @param <T> Type of the list elements
	 * @param initialCapacity The initial capacity of the returned List
	 * @return An instance of {@link List} with an initial capacity of 3
	 * */
	private static <T> List<T> createEmptyValue(int initialCapacity) {
		return new ArrayList<>(initialCapacity);
	}

	/**
	 * @param typeClass The exact class of the elements
	 * @throws IllegalArgumentException When {@code typeClass} is {@link NBTEndTag}{@code .class}
	 * @throws NullPointerException When {@code typeClass} is {@code null}
	 */
	public NBTListTag(Class<? super T> typeClass) throws IllegalArgumentException, NullPointerException {
		super(createEmptyValue(3));
		if (typeClass == NBTEndTag.class) {
			throw new IllegalArgumentException("cannot create ListTag with EndTag elements");
		}
		this.typeClass = Objects.requireNonNull(typeClass);
	}

	public Class<?> getTypeClass() {
		return typeClass == null ? NBTEndTag.class : typeClass;
	}

	public int size() {
		return getValue().size();
	}

	public T remove(int index) {
		return getValue().remove(index);
	}

	public void clear() {
		getValue().clear();
	}

	public boolean contains(T t) {
		return getValue().contains(t);
	}

	public boolean containsAll(Collection<NBTTag<?>> tags) {
		return getValue().containsAll(tags);
	}

	public void sort(Comparator<T> comparator) {
		getValue().sort(comparator);
	}

	@Override
	public Iterator<T> iterator() {
		return getValue().iterator();
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		getValue().forEach(action);
	}

	public T set(int index, T t) {
		return getValue().set(index, Objects.requireNonNull(t));
	}

	/**
	 * Adds a Tag to this ListTag after the last index.
	 * @param t The element to be added.
	 * */
	public void add(T t) {
		add(size(), t);
	}

	public void add(int index, T t) {
		Objects.requireNonNull(t);
		if (typeClass == null || typeClass == NBTEndTag.class) {
			typeClass = t.getClass();
		} else if (typeClass != t.getClass()) {
			throw new ClassCastException(
					String.format("cannot add %s to ListTag<%s>",
							t.getClass().getSimpleName(),
							typeClass.getSimpleName()));
		}
		getValue().add(index, t);
	}

	public void addAll(Collection<T> t) {
		for (T tt : t) {
			add(tt);
		}
	}

	public void addAll(int index, Collection<T> t) {
		int i = 0;
		for (T tt : t) {
			add(index + i, tt);
			i++;
		}
	}

	public void addBoolean(boolean value) {
		addUnchecked(new NBTByteTag(value));
	}

	public void addByte(byte value) {
		addUnchecked(new NBTByteTag(value));
	}

	public void addShort(short value) {
		addUnchecked(new NBTShortTag(value));
	}

	public void addInt(int value) {
		addUnchecked(new NBTIntTag(value));
	}

	public void addLong(long value) {
		addUnchecked(new NBTLongTag(value));
	}

	public void addFloat(float value) {
		addUnchecked(new NBTFloatTag(value));
	}

	public void addDouble(double value) {
		addUnchecked(new NBTDoubleTag(value));
	}

	public void addString(String value) {
		addUnchecked(new NBTStringTag(value));
	}

	public void addByteArray(byte[] value) {
		addUnchecked(new NBTByteArrayTag(value));
	}

	public void addIntArray(int[] value) {
		addUnchecked(new NBTIntArrayTag(value));
	}

	public void addLongArray(long[] value) {
		addUnchecked(new NBTLongArrayTag(value));
	}

	public T get(int index) {
		return getValue().get(index);
	}

	public int indexOf(T t) {
		return getValue().indexOf(t);
	}

	@SuppressWarnings("unchecked")
	public <L extends NBTTag<?>> NBTListTag<L> asTypedList(Class<L> type) {
		checkTypeClass(type);
		typeClass = type;
		return (NBTListTag<L>) this;
	}

	public NBTListTag<NBTByteTag> asByteTagList() {
		return asTypedList(NBTByteTag.class);
	}

	public NBTListTag<NBTShortTag> asShortTagList() {
		return asTypedList(NBTShortTag.class);
	}

	public NBTListTag<NBTIntTag> asIntTagList() {
		return asTypedList(NBTIntTag.class);
	}

	public NBTListTag<NBTLongTag> asLongTagList() {
		return asTypedList(NBTLongTag.class);
	}

	public NBTListTag<NBTFloatTag> asFloatTagList() {
		return asTypedList(NBTFloatTag.class);
	}

	public NBTListTag<NBTDoubleTag> asDoubleTagList() {
		return asTypedList(NBTDoubleTag.class);
	}

	public NBTListTag<NBTStringTag> asStringTagList() {
		return asTypedList(NBTStringTag.class);
	}

	public NBTListTag<NBTByteArrayTag> asByteArrayTagList() {
		return asTypedList(NBTByteArrayTag.class);
	}

	public NBTListTag<NBTIntArrayTag> asIntArrayTagList() {
		return asTypedList(NBTIntArrayTag.class);
	}

	public NBTListTag<NBTLongArrayTag> asLongArrayTagList() {
		return asTypedList(NBTLongArrayTag.class);
	}

	@SuppressWarnings("unchecked")
	public NBTListTag<NBTListTag<?>> asListTagList() {
		checkTypeClass(NBTListTag.class);
		typeClass = NBTListTag.class;
		return (NBTListTag<NBTListTag<?>>) this;
	}

	public NBTListTag<NBTCompoundTag> asCompoundTagList() {
		return asTypedList(NBTCompoundTag.class);
	}

	@Override
	public String valueToString(int maxDepth) {
		StringBuilder sb = new StringBuilder("{\"type\":\"").append(getTypeClass().getSimpleName()).append("\",\"list\":[");
		for (int i = 0; i < size(); i++) {
			sb.append(i > 0 ? "," : "").append(get(i).valueToString(decrementMaxDepth(maxDepth)));
		}
		sb.append("]}");
		return sb.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!super.equals(other) || size() != ((NBTListTag<?>) other).size() || getTypeClass() != ((NBTListTag<?>) other).getTypeClass()) {
			return false;
		}
		for (int i = 0; i < size(); i++) {
			if (!get(i).equals(((NBTListTag<?>) other).get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTypeClass().hashCode(), getValue().hashCode());
	}

	@Override
	public int compareTo(NBTListTag<T> o) {
		return Integer.compare(size(), o.getValue().size());
	}

	@SuppressWarnings("unchecked")
	@Override
	public NBTListTag<T> clone() {
		NBTListTag<T> copy = new NBTListTag<>();
		// assure type safety for clone
		copy.typeClass = typeClass;
		for (T t : getValue()) {
			copy.add((T) t.clone());
		}
		return copy;
	}

	//TODO: make private
	@SuppressWarnings("unchecked")
	public void addUnchecked(NBTTag<?> tag) {
		if (typeClass != null && typeClass != tag.getClass() && typeClass != NBTEndTag.class) {
			throw new IllegalArgumentException(String.format(
					"cannot add %s to ListTag<%s>",
					tag.getClass().getSimpleName(), typeClass.getSimpleName()));
		}
		add(size(), (T) tag);
	}

	private void checkTypeClass(Class<?> clazz) {
		if (typeClass != null && typeClass != NBTEndTag.class && typeClass != clazz) {
			throw new ClassCastException(String.format(
					"cannot cast ListTag<%s> to ListTag<%s>",
					typeClass.getSimpleName(), clazz.getSimpleName()));
		}
	}
}
