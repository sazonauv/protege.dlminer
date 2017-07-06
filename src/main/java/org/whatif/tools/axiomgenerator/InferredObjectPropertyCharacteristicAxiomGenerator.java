package org.whatif.tools.axiomgenerator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyCharacteristicAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredObjectPropertyAxiomGenerator;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.1.0
 */
public class InferredObjectPropertyCharacteristicAxiomGenerator
        extends InferredObjectPropertyAxiomGenerator<OWLObjectPropertyCharacteristicAxiom> {

    @Override
    protected void addAxioms(OWLObjectProperty entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
            Set<OWLObjectPropertyCharacteristicAxiom> result) {
        addIfEntailed(dataFactory.getOWLFunctionalObjectPropertyAxiom(entity), reasoner, result);
        addIfEntailed(dataFactory.getOWLInverseFunctionalObjectPropertyAxiom(entity), reasoner, result);
        addIfEntailed(dataFactory.getOWLSymmetricObjectPropertyAxiom(entity), reasoner, result);
        addIfEntailed(dataFactory.getOWLAsymmetricObjectPropertyAxiom(entity), reasoner, result);
        addTransitiveAxiomIfEntailed(entity, reasoner, dataFactory, result);
        addIfEntailed(dataFactory.getOWLReflexiveObjectPropertyAxiom(entity), reasoner, result);
        addIfEntailed(dataFactory.getOWLIrreflexiveObjectPropertyAxiom(entity), reasoner, result);
    }

    protected static void addTransitiveAxiomIfEntailed(OWLObjectProperty property, OWLReasoner reasoner,
            OWLDataFactory dataFactory, Set<OWLObjectPropertyCharacteristicAxiom> result) {
        OWLObjectPropertyCharacteristicAxiom axiom = dataFactory.getOWLTransitiveObjectPropertyAxiom(property);
        if (reasoner.isEntailmentCheckingSupported(axiom.getAxiomType()) && reasoner.isEntailed(axiom)) {
            if (!triviallyTransitiveCheck(property, reasoner, dataFactory)) {
                result.add(axiom);
            }
        }
    }

    /**
     * Test to see if a property is only vacuously transitive; i.e. if there
     * cannot be any three individuals a,b,c such that foo(a,b) and foo(b,c) -
     * e.g. if the domain and range of foo are disjoint. .
     * 
     * @param property
     *        property to test
     * @param reasoner
     *        reasoner to use for testing
     * @param df
     *        data factory
     * @return true if property is trivially transitive, or if entailment
     *         checking for OWLObjectPropertyAssertionAxioms is not supported.
     */
    private static boolean triviallyTransitiveCheck(OWLObjectProperty property, OWLReasoner reasoner,
            OWLDataFactory df) {
        // create R some (R some owl:Thing) class
        OWLObjectSomeValuesFrom chain = df.getOWLObjectSomeValuesFrom(property,
                df.getOWLObjectSomeValuesFrom(property, df.getOWLThing()));
        // if chain is unsatisfiable, then the property is trivially transitive
        return !reasoner.isSatisfiable(chain);
    }

    protected static void addIfEntailed(OWLObjectPropertyCharacteristicAxiom axiom, OWLReasoner reasoner,
            Set<OWLObjectPropertyCharacteristicAxiom> result) {
        if (reasoner.isEntailmentCheckingSupported(axiom.getAxiomType()) && reasoner.isEntailed(axiom)) {
            result.add(axiom);
        }
    }

    @Override
    public String getLabel() {
        return "Object property characteristics";
    }
}