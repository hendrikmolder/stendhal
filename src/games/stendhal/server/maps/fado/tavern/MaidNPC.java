package games.stendhal.server.maps.fado.tavern;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;


/**
 * Builds the tavern maid NPC.
 *
 * @author timothyb89/kymara
 */
public class MaidNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();


	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}

	//
	// MaidNPC
	//

	private void buildNPC(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC tavernMaid = new SpeakerNPC("Old Mother Helena") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(28, 14));
				nodes.add(new Path.Node(10, 14));
				nodes.add(new Path.Node(10, 26));
				nodes.add(new Path.Node(19, 26));
				nodes.add(new Path.Node(19, 27));
				nodes.add(new Path.Node(20, 27));
				nodes.add(new Path.Node(21, 27));
				nodes.add(new Path.Node(21, 26));
				nodes.add(new Path.Node(28, 26));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				//addGreeting();
				addJob("I am the bar maid for this fair tavern. We sell imported beers and fine food.");
				addHelp("Why not gather some friends and take a break together, you can put your food down and eat from that long table.");
				addQuest("Oh, I don't have time for anything like that.");

				Map<String, Integer> offers = new HashMap<String, Integer>();
				offers.put("beer", 10);
				offers.put("wine", 15);
				offers.put("cherry", 20);
				offers.put("cheese", 20);
				offers.put("bread", 50);
				offers.put("sandwich", 150);
	 
				addSeller(new SellerBehaviour(offers));
				addGoodbye("Goodbye, all you customers do work me hard ...");
			}
		};
		npcs.add(tavernMaid);
		zone.assignRPObjectID(tavernMaid);
		tavernMaid.put("class", "oldmaidnpc");
		tavernMaid.set(10, 16);
		tavernMaid.initHP(100);
		zone.add(tavernMaid);

	}
}
