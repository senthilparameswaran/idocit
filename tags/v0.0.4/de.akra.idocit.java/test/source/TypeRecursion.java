package source;

public class TypeRecursion {

	private TypeRecursion typeRec;

	public void test(TypeRecursion t) {

	}

	public void setTypeRec(TypeRecursion typeRec, TypeRecursion typeRec2) {
		this.typeRec = typeRec;
	}

	public TypeRecursion getTypeRec() {
		return typeRec;
	}
}
