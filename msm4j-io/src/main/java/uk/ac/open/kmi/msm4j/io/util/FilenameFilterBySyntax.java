/*
 * Copyright (c) 2013. Knowledge Media Institute - The Open University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.open.kmi.msm4j.io.util;

import uk.ac.open.kmi.msm4j.io.Syntax;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filter filenames by Syntax
 * <p/>
 * Author: Carlos Pedrinaci (KMi - The Open University)
 * Date: 05/06/2013
 * Time: 22:12
 */
public class FilenameFilterBySyntax implements FilenameFilter {

    private Syntax syntax;

    public FilenameFilterBySyntax(Syntax syntax) {
        this.syntax = syntax;
    }

    @Override
    public boolean accept(File file, String name) {
        return (name.endsWith("." + syntax.getExtension()));
    }
}
