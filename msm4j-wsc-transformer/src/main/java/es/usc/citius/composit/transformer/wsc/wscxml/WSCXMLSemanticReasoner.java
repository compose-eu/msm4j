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

package es.usc.citius.composit.transformer.wsc.wscxml;


import es.usc.citius.composit.transformer.wsc.wscxml.model.XMLInstance;
import es.usc.citius.composit.transformer.wsc.wscxml.model.taxonomy.XMLConcept;
import es.usc.citius.composit.transformer.wsc.wscxml.model.taxonomy.XMLTaxonomy;

import javax.xml.bind.JAXB;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;


public class WSCXMLSemanticReasoner implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7207936476906318913L;
    private transient XMLTaxonomy taxonomy;
    // Concept -> subclasses
    private Map<String, Set<String>> subclasses;
    // Concept -> superclasses
    private Map<String, Set<String>> superclasses;
    // Instance -> concept
    private Map<String, String> instanceConcept;
    // Concept -> instances
    private Map<String, Set<String>> instances;

    public WSCXMLSemanticReasoner(InputStream xmlTaxonomyStream) {
        this.taxonomy = JAXB.unmarshal(xmlTaxonomyStream, XMLTaxonomy.class);
        initialize();
    }

    public WSCXMLSemanticReasoner(File xmlTaxonomyFile) {
        this.taxonomy = JAXB.unmarshal(xmlTaxonomyFile, XMLTaxonomy.class);
        initialize();
    }

    private void initialize() {
        // Initialize maps
        this.subclasses = new HashMap<String, Set<String>>();
        this.superclasses = new HashMap<String, Set<String>>();
        this.instanceConcept = new HashMap<String, String>();
        this.instances = new HashMap<String, Set<String>>();

        // Load all superclasses, subclasses and instances
        populate(taxonomy.getConcept());

    }

    private final Set<String> populate(XMLConcept root) {

        if (root == null) {
            return new HashSet<String>();
        }

        // Convert root to String add String to the subclass map
        String resourceRoot = root.getName();
        Set<String> rootSubclasses = this.subclasses.get(resourceRoot);
        // Initialize if empty
        if (rootSubclasses == null) {
            rootSubclasses = new HashSet<String>();
            // Update subclasses map
            this.subclasses.put(resourceRoot, rootSubclasses);
        }
        // Fill instances
        Set<String> rootInstances = new HashSet<String>();
        if (root.getInstances() != null) {
            for (XMLInstance instance : root.getInstances()) {
                String resourceInstance = instance.getName();
                rootInstances.add(resourceInstance);
                instanceConcept.put(resourceInstance, resourceRoot);
            }
        }
        this.instances.put(resourceRoot, rootInstances);

        // Add each subclass to the map
        if (root.getConcepts() != null) {
            for (XMLConcept subclass : root.getConcepts()) {
                String resourceSubclass = subclass.getName();
                // Add this subclass
                rootSubclasses.add(resourceSubclass);
                // Update superclasses iteratively. Note that WSC does not
                // support
                // multiple inheritance, so each concept has only one single
                // parent
                Set<String> superclassesForConcept = superclasses
                        .get(resourceSubclass);
                // Initialize if empty
                if (superclassesForConcept == null) {
                    superclassesForConcept = new HashSet<String>();
                    superclasses.put(resourceSubclass, superclassesForConcept);
                }
                // Add root and all their parents as superclasses
                superclassesForConcept.add(resourceRoot);
                // All superclasses
                Set<String> indirectSuperclasses = superclasses
                        .get(resourceRoot);
                if (indirectSuperclasses != null) {
                    superclassesForConcept.addAll(indirectSuperclasses);
                }
                // Add indirect subclasses, repeat recursively
                rootSubclasses.addAll(populate(subclass));
            }
        }
        return rootSubclasses;
    }

    public Set<String> getSubclasses(String concept) {
        Set<String> subclasses = this.subclasses.get(concept);
        if (subclasses == null) {
            return new HashSet<String>();
        }
        return new HashSet<String>(subclasses);
    }

    public Set<String> getSuperclasses(String concept) {
        Set<String> superclasses = this.superclasses.get(concept);
        if (superclasses == null) {
            return new HashSet<String>();
        }
        return new HashSet<String>(superclasses);
    }

    public boolean equivalent(String x, String y) {
        return x.equals(y);
    }

    public boolean subsumes(String x, String y) {
        Set<String> superclasses = this.superclasses.get(y);
        if (superclasses == null) {
            return false;
        }
        return superclasses.contains(x);
    }

    public boolean isSubsumedBy(String x, String y) {
        Set<String> subclasses = this.subclasses.get(y);
        if (subclasses == null) {
            return false;
        }
        return subclasses.contains(x);
    }

    public boolean isSubclass(String x, String y) {
        return isSubsumedBy(x, y);
    }

    public boolean isSuperclass(String x, String y) {
        return subsumes(x, y);
    }

    public String getConceptInstance(String resource) {
        String concept = instanceConcept.get(resource);
        if (concept == null) {
            concept = resource;
        }
        return concept;
    }

    public Set<String> getInstances(String resource) {
        Set<String> instances = this.instances.get(resource);
        if (instances == null) {
            return new HashSet<String>();
        }
        return new HashSet<String>(instances);
    }

    public Set<String> getConcepts() {
        return Collections.unmodifiableSet(this.instances.keySet());
    }

    public Set<String> getInstances() {
        return Collections.unmodifiableSet(this.instanceConcept.keySet());
    }
}