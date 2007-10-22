package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Cloak Collector 2
 * <p>
 * PARTICIPANTS: - Josephine, a young woman who live in Ados/Fado
 * <p>
 * STEPS:
 * <ul>
 * <li> Josephine asks you to bring her a cloak in colours she didn't get already from you
 * <li> You bring cloaks to Josephine 
 * <li> Repeat until Josephine
 * received all cloaks. (Of course you can bring several cloaks at the same
 * time.) 
 * <li> Josephine gives you a reward
 * </ul>
 * <p>
 * REWARD: - 105 Karma in all - 25000 XP - secnt (when ready)
 * <p>
 * REPETITIONS: - None.
 */
public class CloakCollector2 extends AbstractQuest {

    private static final List<String> NEEDEDCLOAKS2 = Arrays.asList("red_cloak", "shadow_cloak", "xeno_cloak",
								       "elvish_cloak", "chaos_cloak", "mainio_cloak",
								       "golden_cloak", "black_dragon_cloak");
        private static final String OLD_QUEST = "cloaks_collector";
        private static final String QUEST_SLOT = "cloaks_collector_2";   

	/**
	 * Returns a list of the names of all cloaks that the given player still has
	 * to bring to fulfil the quest.
	 *
	 * @param player
	 *            The player doing the quest
	 * @param hash
	 *            If true, sets a # character in front of every name
	 * @return A list of cloak names
	 */
	private List<String> missingcloaks2(Player player, boolean hash) {

		String doneText2 = player.getQuest(QUEST_SLOT);
		List<String> neededCopy2 = new LinkedList<String>(NEEDEDCLOAKS2);

		if (doneText2 == null) {
			doneText2 = "";
		}
		List<String> done2 = Arrays.asList(doneText2.split(";"));
		neededCopy2.removeAll(done2);
		if (hash) {
			List<String> result2 = new LinkedList<String>();
			for (String cloak : neededCopy2) {
				result2.add("#" + cloak);
			}
			return result2;
		}

		return neededCopy2;
	}

	private void step_1() {
		SpeakerNPC npc = npcs.get("Josephine");

		// player says hi before starting the quest
		npc
				.add(
						ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new SpeakerNPC.ChatCondition() {
							@Override
							public boolean fire(Player player, String text,
									SpeakerNPC engine) {
								return !player.hasQuest(QUEST_SLOT)&&player.isQuestCompleted(OLD_QUEST);
							}
						},
						ConversationStates.QUEST_2_OFFERED,
						"Hi again! I hear there's some new cloaks out, and I'm regretting not asking you about the ones I didn't like before. It feels like my #collection isn't complete...",
						null);

		// player asks what cloaks are needed
		npc.add(ConversationStates.QUEST_2_OFFERED, "collection", null,
				ConversationStates.QUEST_2_OFFERED, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						List<String> needed2 = missingcloaks2(player, true);
						engine.say("It's missing "
								+ Grammar
										.quantityplnoun(needed2.size(), "cloak")
								+ ". That's "
								+ Grammar.enumerateCollection(needed2)
								+ ". Will you find them?");
					}
				});
		// player says yes
		npc.add(ConversationStates.QUEST_2_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.IDLE, null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						engine.say("Brilliant! I'm all excited again! Bye!");
						player.setQuest(QUEST_SLOT, "");
						player.addKarma(5.0);
					}
				});

		// player is not willing to help
		npc.add(ConversationStates.QUEST_2_OFFERED, "no", null,
				ConversationStates.QUEST_2_OFFERED, null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						engine
								.say("Oh ... you're not very friendly. Please say yes?");
						player.addKarma(-5.0);
					}
				});

		// player asks about an individual cloak. We used the trick before that all cloaks were named by colour 
		// (their subclass) - so she would tell them what colour it was. In this case it fails for elvish,
		// xeno and shadow which are not named by colour. So, this time she'll say, e.g.
		// It's a shadow_cloak, sorry if that's not much help, so will you find them all?
		// rather than say for elf cloak she'd said 'It's a white_cloak, so will you find them all?'
		// it will still work for red (red_spotted is the subclass), black dragon (black), 
		// golden, mainio (primary coloured), chaos (multicoloured).
		for (String cloak : NEEDEDCLOAKS2) {
			npc.add(ConversationStates.QUEST_2_OFFERED, cloak, null,
					ConversationStates.QUEST_2_OFFERED, null,
					new SpeakerNPC.ChatAction() {
						@Override
						public void fire(Player player, String text,
								SpeakerNPC engine) {
							engine
									.say("You haven't seen one before? Well, it's a "
											+ StendhalRPWorld.get()
													.getRuleManager()
													.getEntityManager()
													.getItem(text)
													.getItemSubclass()
											+ ". Sorry if that's not much help, it's all I know! So, will you find them all?");
						}
					});
		}
	}

	private void step_2() {
		// Just find the cloaks and bring them to Josephine.
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Josephine");

		// player returns while quest is still active
		npc.add(
				ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text,
							SpeakerNPC engine) {
						return player.hasQuest(QUEST_SLOT)
								&& !player.isQuestCompleted(QUEST_SLOT);
					}
				}, ConversationStates.QUESTION_2,
				"Welcome back! Have you brought any #cloaks with you?", null);
		// player asks what exactly is missing
		npc.add(ConversationStates.QUESTION_2, "cloaks", null,
				ConversationStates.QUESTION_2, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						List<String> needed2 = missingcloaks2(player, true);
						engine.say("I want "
								+ Grammar
										.quantityplnoun(needed2.size(), "cloak")
								+ ". That's "
								+ Grammar.enumerateCollection(needed2)
								+ ". Did you bring any?");
					}
				});
		// player says he has a required cloak with him
		npc.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.QUESTION_2,
				"Woo! What #cloaks did you bring?", null);

		for (String cloak : NEEDEDCLOAKS2) {
			npc.add(ConversationStates.QUESTION_2, cloak, null,
					ConversationStates.QUESTION_2, null,
					new SpeakerNPC.ChatAction() {
						@Override
						public void fire(Player player, String text,
								SpeakerNPC engine) {
							List<String> missing2 = missingcloaks2(player, false);

							if (missing2.contains(text)) {
								if (player.drop(text)) {
									// register cloak as done
									String doneText = player
											.getQuest(QUEST_SLOT);
									player.setQuest(QUEST_SLOT,
											doneText + ";" + text);
									// check if the player has brought all
									// cloaks
									missing2 = missingcloaks2(player, true);
									if (!missing2.isEmpty()) {
										engine
												.say("Wow, thank you! What else did you bring?");
									} else {
										rewardPlayer(player);
										// TODO: Make speech mention scent reward if applicable.
										engine
												.say("Oh, yay! My collection is complete, at least for now! You're so kind, I bet you'll have great Karma now!");
										player.setQuest(QUEST_SLOT,
												"done");
										player.notifyWorldAboutChanges();
										engine
												.setCurrentState(ConversationStates.ATTENDING);
									}
								} else {
									engine
											.say("Oh, I'm disappointed. You don't really have "
													+ Grammar.a_noun(text)
													+ " with you.");
								}
							} else {
								engine
										.say("You're terribly forgetful, you already brought that one to me.");
							}
						}


					});
		}

		npc.add(ConversationStates.ATTENDING, "no",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text,
							SpeakerNPC engine) {
						return !player.isQuestCompleted(QUEST_SLOT);
					}
				}, ConversationStates.ATTENDING,
				"Ok. If you want help, just say.", null);

		// player says he didn't bring any cloaks to different question
		npc.add(ConversationStates.QUESTION_2, Arrays.asList("no", "nothing"),
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text,
							SpeakerNPC engine) {
						return !player.isQuestCompleted(QUEST_SLOT);
					}
				}, ConversationStates.ATTENDING, "Okay then. Come back later.",
				null);

		// player returns after finishing the quest
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text,
							SpeakerNPC engine) {
						return player.isQuestCompleted(QUEST_SLOT);
					}
				}, ConversationStates.ATTENDING,
				"Hi again, lovely! All my cloaks still look great! Thanks!", null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		step_1();
		step_2();
		step_3();
	}
	private static void rewardPlayer(Player player) {
	    //  TODO: Once scent is done, add this as reward. Note it might be stackable, so check! 
	    //	Item scent = StendhalRPWorld.get()
	    //			.getRuleManager()
	    //			.getEntityManager().getItem("scent");
	    //	scent.setBoundTo(player.getName());
	    //	player.equip(scent, true);
		player.addKarma(100.0);
		player.addXP(25000);
      	}
}
