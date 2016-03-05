/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kotlin.jvm.internal;

import java.lang.Object;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@Deprecated
@kotlin.Deprecated(message = "This class supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
public class SpreadBuilder {

    private final ArrayList<Object> list;

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public SpreadBuilder(int size) {
        list = new ArrayList<Object>(size);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public void addSpread(Object container) {
        if (container == null) return;

        if (container instanceof Object[]) {
            Object[] array = (Object[]) container;
            if (array.length > 0) {
                list.ensureCapacity(list.size() + array.length);
                for (Object element : array) {
                    list.add(element);
                }
            }
        }
        else if (container instanceof Collection) {
            list.addAll((Collection) container);
        }
        else if (container instanceof Iterable) {
            for (Object element : (Iterable) container) {
                list.add(element);
            }
        }
        else if (container instanceof Iterator) {
            for (Iterator iterator = (Iterator) container; iterator.hasNext(); ) {
                list.add(iterator.next());
            }
        }
        else {
            throw new UnsupportedOperationException("Don't know how to spread " + container.getClass());
        }
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public int size() {
        return list.size();
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public void add(Object element) {
        list.add(element);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public Object[] toArray(Object[] a) {
        return list.toArray(a);
    }
}
