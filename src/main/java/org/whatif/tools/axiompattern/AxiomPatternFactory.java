package org.whatif.tools.axiompattern;

import java.util.HashSet;
import java.util.Set;

public class AxiomPatternFactory {

	private static final AxiomPattern no = new NoAxiomPattern();
	private static final AxiomPattern unsat = new UnsatisfiableClassAxiomPattern();
	private static final AxiomPattern disjointdirect = new DisjointClassDirectAxiomPattern();
	private static final AxiomPattern disjointindirect = new DisjointClassIndirectAxiomPattern();
	private static final AxiomPattern mostspectype = new MostSpecificTypeAxiomPattern();
	private static final AxiomPattern inconsistent = new InconsistentOntologyClassAxiomPattern();
	private static final AxiomPattern propertychar = new PropertyCharacteristicAxiomPattern();
	private static final AxiomPattern subifeq = new SubClassIfEquivalentClassAxiomPattern();
	private static final AxiomPattern subifnoteq = new SubClassIfNotEquivalentClassAxiomPattern();
	private static final AxiomPattern eq = new EquivalentClassAxiomPattern();
	private static final AxiomPattern asserted = new AssertedAxiomPattern();
	private static final AxiomPattern propeqinv = new PropertyEqualsItsInverseAxiomPattern();

	private static final AxiomPattern stray = new AddedStrayAxiomPattern();
	private static final AxiomPattern taut = new TautologyAxiomPattern();
	//private static final AxiomPattern express = new ViolatesOWLDLAxiomPattern();
	private static final AxiomPattern redundant = new AddedAxiomRedundantPattern();

	private static final AxiomPattern dl = new ViolatesOWLDLAxiomPattern();
	private static final AxiomPattern el = new ViolatesOWLELAxiomPattern();
	private static final AxiomPattern ql = new ViolatesOWLQLAxiomPattern();
	private static final AxiomPattern rl = new ViolatesOWLRLAxiomPattern();

	public static Set<AxiomPattern> getAllPatterns() {
		Set<AxiomPattern> patterns = new HashSet<AxiomPattern>();
		patterns.add(unsat);
		patterns.add(disjointdirect);
		patterns.add(disjointindirect);
		patterns.add(mostspectype);
		patterns.add(inconsistent);
		patterns.add(propertychar);
		patterns.add(subifeq);
		patterns.add(subifnoteq);
		patterns.add(eq);
		patterns.add(asserted);
		patterns.add(propeqinv);
		return patterns;
	}

	public static Set<AxiomPattern> getChangesetPatterns() {
		Set<AxiomPattern> patterns = new HashSet<AxiomPattern>();
		patterns.add(taut);
		patterns.add(stray);
		//patterns.add(express);
		patterns.add(redundant);

		patterns.add(dl);
		patterns.add(ql);
		patterns.add(rl);
		patterns.add(el);
		return patterns;
	}

	/**
	 * @return the asserted
	 */
	public static AxiomPattern getAssertedAxiomPattern() {
		return asserted;
	}

	/**
	 * @return the unsat
	 */
	public static AxiomPattern getUnsatisfiableAxiomPattern() {
		return unsat;
	}

	/**
	 * @return the disjointdirect
	 */
	public static AxiomPattern getDisjointClassDirectPattern() {
		return disjointdirect;
	}

	/**
	 * @return the disjointindirect
	 */
	public static AxiomPattern getDisjointClassIndirectPattern() {
		return disjointindirect;
	}

	/**
	 * @return the mostspectype
	 */
	public static AxiomPattern getMostSpecificTypeAxiomPattern() {
		return mostspectype;
	}

	/**
	 * @return the inconsistent
	 */
	public static AxiomPattern getInconsistentOntologyAxiomPattern() {
		return inconsistent;
	}

	/**
	 * @return the propertychar
	 */
	public static AxiomPattern getPropertyCharacteristicAxiomPattern() {
		return propertychar;
	}

	/**
	 * @return the subifeq
	 */
	public static AxiomPattern getSubClassIfEqualAxiomPattern() {
		return subifeq;
	}

	/**
	 * @return the subifnoteq
	 */
	public static AxiomPattern getSubClassIfNotEqualAxiomPattern() {
		return subifnoteq;
	}

	/**
	 * @return the eq
	 */
	public static AxiomPattern getEquivalenceClassAxiomPattern() {
		return eq;
	}

	/**
	 * @return the stray
	 */
	public static AxiomPattern getAddedStrayEntityPattern() {
		return stray;
	}

	/**
	 * @return the taut
	 */
	public static AxiomPattern getAddedTautologyPattern() {
		return taut;
	}

	/**
	 * @return the express
	 
	public static AxiomPattern getAddedAxiomIncreasedProfilePattern() {
		return express;
	}
*/
	/**
	 * @return the redundant
	 */
	public static AxiomPattern getAddedAxiomAlreadyImpliedPattern() {
		return redundant;
	}

	public static AxiomPattern getDefaultPattern() {
		return no;
	}

	public static AxiomPattern getDLProfilePattern() {
		return dl;
	}

	public static AxiomPattern getELProfilePattern() {
		return el;
	}

	public static AxiomPattern getQLProfilePattern() {
		return ql;
	}

	public static AxiomPattern getRLProfilePattern() {
		return rl;
	}
	
	public static AxiomPattern getPropertyEqualsInversePattern() {
		return propeqinv;
	}
}
