package me.dueris.calio.util.holders;

public record TriPair<A, B, C>(A a, B b, C c) {

	public A first() {
		return a();
	}

	public B second() {
		return b();
	}

	public C third() {
		return c();
	}
}
