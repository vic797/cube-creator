package io.github.cubecreator.util

abstract class AbstractObjectDescriptor<T> implements ObjectDescriptor {

    final T object

    AbstractObjectDescriptor(T object) {
        this.object = object
    }

}
