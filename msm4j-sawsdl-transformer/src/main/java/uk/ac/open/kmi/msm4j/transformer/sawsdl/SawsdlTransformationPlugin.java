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

package uk.ac.open.kmi.msm4j.transformer.sawsdl;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import uk.ac.open.kmi.msm4j.io.ServiceTransformer;
import uk.ac.open.kmi.msm4j.io.TransformationPluginModule;

/**
 * SawsdlTransformationPlugin is a Guice module providing support for transforming SAWSDL services into MSM
 *
 * @author <a href="mailto:carlos.pedrinaci@open.ac.uk">Carlos Pedrinaci</a> (KMi - The Open University)
 * @since 19/07/2013
 */
public class SawsdlTransformationPlugin extends AbstractModule implements TransformationPluginModule {

    @Override
    protected void configure() {
        MapBinder<String, ServiceTransformer> binder = MapBinder.newMapBinder(binder(), String.class, ServiceTransformer.class);
        binder.addBinding(SawsdlTransformer.mediaType).to(SawsdlTransformer.class);
    }
}
