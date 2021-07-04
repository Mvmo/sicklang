package dev.mvmo.sicklang.internal.object.hashkey;

import dev.mvmo.sicklang.internal.object.ObjectType;

// TODO: Cache
public record HashKey(ObjectType type, int value) {
}
