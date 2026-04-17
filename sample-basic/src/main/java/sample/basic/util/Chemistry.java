package sample.basic.util;

@SuppressWarnings("unused"/*helps with testing the chemistry plugin*/)
public class Chemistry {
    /*SMILES*/
    private static final String MOL_11 = "O";//water
    private static final String MOL_12 = "O=O";//dioxygen
    private static final String MOL_13 = "C([C@@H]1[C@H]([C@@H]([C@H]([C@H](O1)O)O)O)O)O";//α-d-glucopyranose
    private static final String MOL_14 = "OC[C@H]1OC(O)[C@H](O)[C@@H](O)[C@@H]1O";//β-d-glucopyranose
    private static final String MOL_15 = "CCCCCCCCCCCCCCCC(OCC(OC(CCCCCCC/C=C/CCCCCCCC)=O)COP(OCC[N+](C)(C)C)([O-])=O)=O";

    /*InChI*/
    private static final String MOL_21 = "InChI=1S/H2O/h1H2";//water
    private static final String MOL_22 = "InChI=1S/C6H12O6/c7-1-2-3(8)4(9)5(10)6(11)12-2/h2-11H,1H2/t2-,3-,4+,5-,6?/m1/s1";//glucose
}
