package fr.dwg.discordbot.discord;

/**
 * Texte unique pour {@code /help} et le message à poster dans un salon Discord.
 * Limite Discord : 2000 caractères.
 */
public final class DiscordSlashTexts {

    public static final String HELP = """
            **DidiBot — commandes slash**

            Tape `/` dans un salon, puis choisis une commande.

            **Commandes**
            • `/help` — ce guide (visible uniquement pour toi)
            • `/ping` — bot en ligne + latence
            • `/triggers` — règles actives sur **ce** serveur (locales + globales)
            • `/stats` — réponses du jour, 7 jours, top triggers et top trolleurs
            • `/propose-trigger` — proposer une règle (un admin doit l’approuver)

            **Proposer une règle**
            Exemple :
            `/propose-trigger nom:Troll Rust motif:Rust reponse:Ferris a entendu ça. type:Contient`

            • `nom` — nom affiché dans le panel
            • `motif` — mot ou expression à détecter
            • `reponse` — ce que DidiBot répondra
            • `type` — Contient / Exact / Commence par / Regex (défaut : Contient)

            La proposition reste en attente jusqu’à validation admin.

            **Réponses auto**
            DidiBot lit les messages (pas ceux des bots). Si un motif matche, il répond.
            Environ 1 % des réponses sont des événements rares. Un cooldown évite le spam.

            **Astuces**
            • Les règles **Global** s’appliquent partout, sauf si le même motif existe déjà en local.
            • Pour tester, propose d’abord — n’envoie pas le motif en boucle.
            """;

    private DiscordSlashTexts() {
    }
}
