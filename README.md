Tic-Tac-Tornadoes
=================

This is a touchscreen implementation of the game Tic-Tac-Tornadoes
The goal is to get 5 pieces (tornadoes don't count) in a row
Board is 7 by 7, each player starts with 3 tornadoes and 2 tornado cancels
On a player's turn, that player can:
1. Place a piece. (If placed under the influence of an opposing tornado, it immediately becomes a ghost.)
2. Place a tornado at the cost of 1 tornado. That converts all pieces of the opposing type on its row or column to ghosts. A ghost is effectively an empty space except that it becomes a regular piece if the tornado's effect is lifted. A ghost is denoted by a small version of a piece in the upper right corner of the square.
3. Place a tornado cancel at the cost of 1 tornado cancel. This eliminates the effect of an opposing tornado, replacing it with a blocked square that cannot be placed on but has no effect.
If 2 tornadoes are on the same row or column, they block the other's effect and neither affects the area between them.

Implementation:
InstructionsActivity just gives you a screen with the rules. It's brought up by pressing the Rules button in-game.
GameStartActivity is like InstructionsActivity but with options at the bottom to select whether a player or an AI of some difficulty is on each side (each side is set independently so you can create any match you want), and a start game button. It's the default screen that you go to when starting the app or finishing a game.
TTTAndroidActivity is the activity where most of the stuff actually happens. It makes and displays the rules and quit buttons, the radio buttons for what type of move you're doing, and the game board. Everything in-game but the implementation of the game rules is here. Board input is done by messaging to the UI thread whenever a move is made (so the AIs run on a separate thread so that they can message the UI one; the message is actually a semaphore as it's called by both AI and player moves).
AIs are implemented in TTTAndroidActivity, except the stupid one which is in StupidAI because it's unexpectedly long. The stupid one plays randomly. The easy one looks two moves into the future and picks the best-looking one using minimax, weighting all squares the same. The medium one is similar but continues looking ahead until a predetermined amount of time has passed, and it weights the squares based on actual values determined empirically. The hard one is similar to the medium one but uses alpha-beta pruning, which means that it often searches deeper and therefore plays better. The MoveValue class is used as an internal return type for the AIs, it contains both a move and the strength value associated with it.
Board and BoardPiece handle the implementation of the rules. Board holds all the variables about the game at the time and has the play function, which takes in a move and modifies the board accordingly. That's mostly pretty simple, but the tornado implementation is complex. When a tornado or TC is placed, it recalculates the board entirely, modifying actual visible values and also storing, for each BoardPiece, what types it is allowed or not allowed to have for pieces. (The BoardPiece class basically holds values like what it holds and what it's allowed to hold, and what image it currently shows.) Then when a piece is played, it checks to see if it's possible to see if it should be ghosted or not.
Util has some debug logging functions in it. It is where generic functions that aren't related to any part of the application in particular go. It's where the static final debug flag is set.
OldBoard, OldBoardPiece, AbstractBoard, and AbstractBoardPiece aren't used. They're old versions of Board and BoardPiece. CopyOfTTTAndroidActivity isn't used for anything either.
