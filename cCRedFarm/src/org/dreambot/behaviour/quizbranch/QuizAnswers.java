package org.dreambot.behaviour.quizbranch;

import java.util.HashMap;

/**
 * @author camalCase
 * class that will use a hashmap to return answers for natural history quiz questions
 */
public class QuizAnswers {
    public static HashMap<String, String> answersMap = new HashMap<String, String>() {{
        // Lizard
        put("How does a lizard regulate body heat?", "Sunlight");
        put("Who discovered how to kill lizards?", "The Slayer Masters");
        put("How many eyes does a lizard have?", "Three");
        put("What order do lizards belong to?", "Squamata");
        put("What happens when a lizard becomes cold?", "It becomes sleepy");
        put("Lizard skin is made of the same substance as?", "Hair");

        // Battle tortoise
        put("What is the name of the oldest tortoise ever recorded?", "Mibbiwocket");
        put("What is a tortoise's favourite food?", "Vegetables");
        put("Name the explorer who discovered the world's oldest tortoise.", "Admiral Bake");
        put("How does the tortoise protect itself?", "Hard shell");
        put("If a tortoise had twenty rings on its shell, how old would it be?", "Twenty years");
        put("Which race breeds tortoises for battle?", "Gnomes");

        // Dragon
        put("What is considered a delicacy by dragons?", "Runite");
        put("What is the best defence against a dragon's attack?", "Anti dragon-breath shield");
        put("How long do dragons live?", "Unknown");
        put("Which of these is not a type of dragon?", "Elemental.");
        put("What is the favoured territory of a dragon?", "Old battle sites");
        put("Approximately how many feet tall do dragons stand?", "Twelve");

        // Wyvern
        put("How did the wyverns die out?", "Climate change");
        put("How many legs does a wyvern have?", "Two");
        put("Where have wyvern bones been found?", "Asgarnia");
        put("Which genus does the wyvern theoretically belong to?", "Reptiles");
        put("What are the wyverns' closest relations?", "Dragons");
        put("What is the ambient temperature of wyvern bones?", "Below room temperature");

        // EAST ROOM
        // Snail
        put("What is special about the shell of the giant Morytanian snail?", "It is resistant to acid");
        put("How do Morytanian snails capture their prey?", "Spitting acid");
        put("Which of these is a snail byproduct?", "Fireproof oil");
        put("What does 'Achatina Acidia' mean?", "Acid-spitting snail");
        put("How do snails move?", "Contracting and stretching");
        put("What is the 'trapdoor', which snails use to cover the entrance to their shells called?", "An operculum");

        // Snake
        put("What is snake venom adapted from?", "Stomach acid");

        // 20:09:14: [S Aside from their noses, what do snakes use to smell?
        put("Aside from their noses, what do snakes use to smell?", "Tongue");
        put("If a snake sticks its tongue out at you, what is it doing?", "Seeing how you smell");
        put("If some snakes use venom to kill their prey, what do other snakes use?", "Constriction");
        put("Lizards and snakes belong to the same order - what is it?", "Squamata");
        put("Which habitat do snakes prefer?", "Anywhere");

        // Sea Slug
        put("We assume that sea slugs have a stinging organ on their soft skin - what is it called?", "Nematocysts");
        put("Why has the museum never examined a live sea slug?", "The researchers keep vanishing");
        put("What do we think the sea slug feeds upon?", "Seaweed");
        put("What are the two fangs presumed to be used for?", "Defence or display.");
        put("Off of which coastline would you find sea slugs?", "Ardougne");
        put("In what way are sea slugs similar to snails?", "They have a hard shell");


        // Monkey
        put("Which type of primates do monkeys belong to?", "Simian");
        put("Which have the lighter colour: Karamjan or Harmless monkeys?", "Harmless.");
        put("Monkeys love bananas. What else do they like to eat?", "Bitternuts");
        put("There are two known families of monkeys. One is Karamjan, the other is...?", "Harmless");
        put("What colour mohawk do Karamjan monkeys have?", "Red");
        put("What have Karamjan monkeys taken a deep dislike to?", "Seaweed");

        // SOUTH ROOM
        // Kalphite Queen
        put("Kalphites are ruled by a...?", "Pasha.");
        put("What is the lowest caste in kalphite society?", "Worker");
        put("What are the armoured plates on a kalphite called?", "Lamellae");
//        20:54:29: [S  Are kalphites carnivores, herbivores or omnivores?
        put("Are kalphites carnivores, herbivores or omnivores?", "Carnivores");
        put("What are kalphites assumed to have evolved from?", "Scarab beetles");
        put("Name the prominent figure in kalphite mythology?", "Scabaras");

        // Terrorbird
        put("What is a terrorbird's preferred food?", "Anything");
        put("Who use terrorbirds as mounts?", "Gnomes");
        put("Where do terrorbirds get most of their water?", "Eating plants");
        put("How many claws do terrorbirds have?", "Four");
        put("What do terrorbirds eat to aid digestion?", "Stones");
        put("How many teeth do terrorbirds have?", "0.");

        // WEST ROOM
        // Penguin
        put("Which sense do penguins rely on when hunting?", "Sight");
        put("Which skill seems unusual for the penguins to possess?", "Planning");
        put("How do penguins keep warm?", "A layer of fat");
        put("What is the preferred climate for penguins?", "Cold");
        put("Describe the behaviour of penguins?", "Social");
        put("When do penguins fast?", "During breeding");

        // Mole
        put("What habitat do moles prefer?", "Subterranean");
        put("Why are moles considered to be an agricultural pest?", "They dig holes");
        put("Who discovered giant moles?", "Wyson the Gardener");
        put("What would you call a group of young moles?", "A labour");
        put("What is a mole's favourite food?", "Insects and other invertebrates");
        put("Which family do moles belong to?", "The Talpidae family");

        // Camel (like me!)
        put("What is produced by feeding chilli to a camel?", "Toxic dung");
        put("If an ugthanki has one, how many does a bactrian have?", "Two");
        put("Camels: herbivore, carnivore or omnivore?", "Omnivore");
        put("What is the usual mood for a camel?", "Annoyed");
        put("Where would you find an ugthanki?", "Al Kharid");
        put("Which camel byproduct is known to be very nutritious?", "Milk");

        // Leech
        put("What is the favoured habitat of leeches?", "Water");
        put("What shape is the inside of a leech's mouth?", "'Y'-shaped");
        put("Which of these is not eaten by leeches?", "Apples");
        put("What contributed to the giant growth of Morytanian leeches?", "Environment");
        put("What is special about Morytanian leeches?", "They attack by jumping.");
        put("How does a leech change when it feeds?", "It doubles in size");

    }};

    public static String getAnswer(String question) {
        return answersMap.get(question);
    }

//    public static void main(String[] args) {
//        QuizAnswers quizAnswers = new QuizAnswers();
////        20:14:09: [SCRIPT] Aside from their noses, what do snakes use to smell?
//        System.out.println(quizAnswers.getAnswer("Aside from their noses, what do snakes use to smell?"));
//        System.out.println(quizAnswers.getAnswer("How does a leech change when it feeds?"));
//
//    }
}
