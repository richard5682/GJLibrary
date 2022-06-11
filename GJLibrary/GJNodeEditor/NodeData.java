package GJNodeEditor;

public abstract class NodeData<T> {
	Class<T> targetType;
	@SuppressWarnings("hiding")
	public abstract <T> T getValue();
}
