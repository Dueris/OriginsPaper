package me.dueris.calio.util.holders;

public record TriPair<A, B, C>(A a, B b, C c) {
	public A first() {
		return this.a();
	}

	public B second() {
		return this.b();
	}

	public C third() {
		return this.c();
	}
}
