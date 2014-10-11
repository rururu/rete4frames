package test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import clojure.lang.RT;
import clojure.lang.Var;

public class ReteTest {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args){
        try
        {
        Var.pushThreadBindings(
                        RT.map(RT.CURRENT_NS, RT.CURRENT_NS.get()));
        System.out.println("\n MONKEY AND BANANAS \n");
		rete.jin.reteApp("run", "/home/ru/clojure/rete4frames/examples/mab.clj");
        System.out.println("\n ZEBRA \n");
		rete.jin.reteApp("run", "/home/ru/clojure/rete4frames/examples/zebra.clj");
        System.out.println("\n Mrs. MANNERS \n");
		rete.jin.reteApp("run", "/home/ru/clojure/rete4frames/examples/manners.clj");
        System.out.println("\n WALTZ \n");
		rete.jin.reteApp("run", "/home/ru/clojure/rete4frames/examples/waltz.clj");
        System.out.println("\n MONKEY AND BANANAS 2 \n");
        // First run MAB with empty fact list
		rete.jin.reteAppFacts("run", "/home/ru/clojure/rete4frames/examples/mab.clj", "/home/ru/clojure/rete4frames/examples/zero_facts.clj");
		// Second assert facts into working memory
		// fact1: (monkey location t5-7 on-top-of green-couch holding blank)
		HashMap slots = new HashMap();
		slots.put("location", "t5-7");
		slots.put("on-top-of", "green-couch");
		slots.put("holding", "blank");
		rete.jin.assertFact("monkey", slots);
		// fact2: (thing name green-couch location t5-7 weight heavy on-top-of floor)
		slots = new HashMap();
		slots.put("name", "green-couch");
		slots.put("location", "t5-7");
		slots.put("weight", "heavy");
		slots.put("on-top-of", "floor");
		rete.jin.assertFact("thing", slots);
		// fact3: (thing name red-couch location t2-2 weight heavy on-top-of floor)
		slots = new HashMap();
		slots.put("name", "red-couch");
		slots.put("location", "t2-2");
		slots.put("weight", "heavy");
		slots.put("on-top-of", "floor");
		rete.jin.assertFact("thing", slots);
		// fact4: (thing name big-pillow location t2-2 on-top-of red-couch weight light)
		slots = new HashMap();
		slots.put("name", "big-pillow");
		slots.put("location", "t2-2");
		slots.put("weight", "light");
		slots.put("on-top-of", "red-couch");
		rete.jin.assertFact("thing", slots);
		// fact5: (thing name red-chest location t2-2 on-top-of big-pillow weight light)
		slots = new HashMap();
		slots.put("name", "red-chest");
		slots.put("location", "t2-2");
		slots.put("weight", "light");
		slots.put("on-top-of", "big-pillow");
		rete.jin.assertFact("thing", slots);
		// fact6: (chest name red-chest contents ladder unlocked-by red-key)
		slots = new HashMap();
		slots.put("name", "red-chest");
		slots.put("contents", "ladder");
		slots.put("unlocked-by", "red-key");
		rete.jin.assertFact("chest", slots);
		// fact7: (thing name blue-chest location t7-7 on-top-of ceiling weight light)
		slots = new HashMap();
		slots.put("name", "blue-chest");
		slots.put("location", "t7-7");
		slots.put("weight", "light");
		slots.put("on-top-of", "ceiling");
		rete.jin.assertFact("thing", slots);
		// fact8: (chest name blue-chest contents bananas unlocked-by blue-key)
		slots = new HashMap();
		slots.put("name", "blue-chest");
		slots.put("contents", "bananas");
		slots.put("unlocked-by", "blue-key");
		rete.jin.assertFact("chest", slots);
		// fact9: (thing name blue-couch location t8-8 weight heavy on-top-of floor)
		slots = new HashMap();
		slots.put("name", "blue-couch");
		slots.put("location", "t8-8");
		slots.put("weight", "heavy");
		slots.put("on-top-of", "floor");
		rete.jin.assertFact("thing", slots);
		// fact10: (thing name green-chest location t8-8 on-top-of ceiling weight light)
		slots = new HashMap();
		slots.put("name", "green-chest");
		slots.put("location", "t8-8");
		slots.put("weight", "light");
		slots.put("on-top-of", "ceiling");
		rete.jin.assertFact("thing", slots);
		// fact11: (chest name green-chest contents blue-key unlocked-by red-key)
		slots = new HashMap();
		slots.put("name", "green-chest");
		slots.put("contents", "blue-key");
		slots.put("unlocked-by", "red-key");
		rete.jin.assertFact("chest", slots);
		// fact12: (thing name red-key location t1-3 on-top-of floor weight light)
		slots = new HashMap();
		slots.put("name", "red-key");
		slots.put("location", "t1-3");
		slots.put("weight", "light");
		slots.put("on-top-of", "floor");
		rete.jin.assertFact("thing", slots);
		// fact13: (goal-is-to action eat argument1 bananas)
		slots = new HashMap();
		slots.put("action", "eat");
		slots.put("argument1", "bananas");
		rete.jin.assertFact("goal-is-to", slots);
		System.out.println("\n FIRE 9 RULES \n");
		rete.jin.fire(9);
		System.out.println("\n FIRE 4 RULES \n");
		rete.jin.fire(4);
		System.out.println("\n FIRE 11 RULES \n");
		rete.jin.fire(11);
		System.out.println("\n FIRE REST RULES \n");
		rete.jin.fireAll();
		System.out.println("\n REMAINED IN MEMORY FACTS  \n");
		HashMap afacts = rete.jin.allFacts();
		Set types = afacts.keySet();
		for(Object type: types){
			Collection tfacts = (Collection) afacts.get(type);
			for(Object tfact: tfacts)
				System.out.println(type+" "+tfact);
		}
        System.out.println("\n CHEST FINAL FACTS \n");
		Collection chests = rete.jin.factsOfType("chest");
		for(Object chest: chests)
			System.out.println("chest: "+chest);
		}
        catch(Exception e)
        {
        e.printStackTrace();
        }
        finally
        {
        Var.popThreadBindings();
        }
	}
}
