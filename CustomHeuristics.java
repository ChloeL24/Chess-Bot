package src.pa2.heuristics;

//adding some imports 
import edu.bu.chess.utils.Coordinate;
import edu.cwru.sepia.util.Direction;
import edu.bu.chess.game.move.PromotePawnMove;
import edu.bu.chess.game.piece.Piece;
import edu.bu.chess.game.piece.PieceType;
import edu.bu.chess.game.player.Player;
// SYSTEM IMPORTS
import edu.bu.chess.search.DFSTreeNode;


// JAVA PROJECT IMPORTS
import src.pa2.heuristics.DefaultHeuristics;


public class CustomHeuristics
    extends Object
{

	/**
	 * TODO: implement me! The heuristics that I wrote are useful, but not very good for a good chessbot.
	 * Please use this class to add your heuristics here! I recommend taking a look at the ones I provided for you
	 * in DefaultHeuristics.java (which is in the same directory as this file)
	 */


	 /**
	 * Get the max player from a node
	 * @param node
	 * @return
	 */
	public static Player getMaxPlayer(DFSTreeNode node) //i added
	{
		return node.getMaxPlayer();
	}
//-------------------------------------------my methods--------------------------------------------
	/**
	//get a min player from a node
	*@param node
	*@return
	*/

	public static Player getMinPlayer(DFSTreeNode node){
		return CustomHeuristics.getMaxPlayer(node).equals(node.getGame().getCurrentPlayer()) ? node.getGame().getOtherPlayer() : node.getGame().getCurrentPlayer();
	}

	public static class OffensiveHeuristics extends Object
	{
		public static int getNumberOfPiecesMaxPlayerIsThreatening(DFSTreeNode node){
		int numPiecesMaxPlayerIsThreatening = 0;
		for(Piece peice: node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node)))
		{
			numPiecesMaxPlayerIsThreatening += peice.getAllCaptureMoves(node.getGame()).size();
		}
		return numPiecesMaxPlayerIsThreatening;
		}


		//maybe write a method here that returns an int based on our overall strength. based on how many of our pieces we have
	}

	public static class DefensiveHeuristics extends Object //capability of defending itself
	{

		public static int getNumberOfMaxPlayersAlivePieces(DFSTreeNode node)
		{
			int numMaxPlayersPiecesAlive = 0;
			for (PieceType pieceType : PieceType.values())
			{
				numMaxPlayersPiecesAlive +=node.getGame().getNumberOfAlivePieces(DefaultHeuristics.getMaxPlayer(node), pieceType);
			}
			return numMaxPlayersPiecesAlive;
		}

	public static int getNumberOfMinPlayersAlivePieces(DFSTreeNode node)
	{
		int numMaxPlayersPiecesAlive = 0;
			for(PieceType pieceType : PieceType.values())
			{
				numMaxPlayersPiecesAlive += node.getGame().getNumberOfAlivePieces(CustomHeuristics.getMinPlayer(node), pieceType);
			}
			return numMaxPlayersPiecesAlive;
	}

	//maybe add a method that takes the type/num of player into account. for example, more powerful if we have our queen
	public static boolean hasQueen(DFSTreeNode node)
	{
		boolean has = true;
		Piece queenPiece = node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node), PieceType.QUEEN).iterator().next();
		if (queenPiece == null){
			has = false;
		}
		return has;
	}
	public static int numBishops(DFSTreeNode node) //tells us how many bishops we have left
	{
		int numBishops = 0;
		for (PieceType pieceType : PieceType.values())
		{
			Piece bishop =  node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node), PieceType.BISHOP).iterator().next();
			if (bishop != null){
				numBishops +=1;
			}
		}
		return numBishops;
	}

	public static int getClampedPieceValueTotalSurroundingMaxPlayersKing(DFSTreeNode node)
		{
			// what is the state of the pieces next to the king? add up the values of the neighboring pieces
			// positive value for friendly pieces and negative value for enemy pieces (will clamp at 0)
			int maxPlayerKingSurroundingPiecesValueTotal = 0;

			Piece kingPiece = node.getGame().getBoard().getPieces(DefaultHeuristics.getMaxPlayer(node), PieceType.KING).iterator().next();
			Coordinate kingPosition = node.getGame().getCurrentPosition(kingPiece);
			for(Direction direction : Direction.values())
			{
				Coordinate neightborPosition = kingPosition.getNeighbor(direction);
				if(node.getGame().getBoard().isInbounds(neightborPosition) && node.getGame().getBoard().isPositionOccupied(neightborPosition))
				{
					Piece piece = node.getGame().getBoard().getPieceAtPosition(neightborPosition);
					int pieceValue = Piece.getPointValue(piece.getType());
					if(piece != null && kingPiece.isEnemyPiece(piece))
					{
						maxPlayerKingSurroundingPiecesValueTotal -= pieceValue;
					} else if(piece != null && !kingPiece.isEnemyPiece(piece))
					{
						maxPlayerKingSurroundingPiecesValueTotal += pieceValue;
					}
				}
			}
			// kingSurroundingPiecesValueTotal cannot be < 0 b/c the utility of losing a game is 0, so all of our utility values should be at least 0
			maxPlayerKingSurroundingPiecesValueTotal = Math.max(maxPlayerKingSurroundingPiecesValueTotal, 0);
			return maxPlayerKingSurroundingPiecesValueTotal;
		}


	public static int getNumberOfPiecesThreateningMaxPlayer(DFSTreeNode node)
	{
		//num of pieces threatening us
		int numPiecesThreateningMaxPlayer=0;

		for(Piece piece :node.getGame().getBoard().getPieces(CustomHeuristics.getMinPlayer(node)))
		{
			numPiecesThreateningMaxPlayer += piece.getAllCaptureMoves(node.getGame()).size();
		}
		return numPiecesThreateningMaxPlayer;
	}

}

//maybe see how in-danger our queen is?
	public static int getNumberOfPiecesThreateningQueen(DFSTreeNode node)
	{
		int num = 0;
		Piece queenPiece = node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node), PieceType.QUEEN).iterator().next();

		num = queenPiece.getAllCaptureMoves(node.getGame()).size();
		return num;

	}


public static double getOffensiveMaxPlayerHeuristicValue(DFSTreeNode node){
	double damageDealtInThisNode = node.getGame().getBoard().getPointsEarned(CustomHeuristics.getMaxPlayer(node));

	switch(node.getMove().getType())
		{
		case PROMOTEPAWNMOVE:
			PromotePawnMove promoteMove = (PromotePawnMove)node.getMove();
			damageDealtInThisNode += Piece.getPointValue(promoteMove.getPromotedPieceType());
			break;
		default:
			break;
		}
		// offense can typically include the number of pieces that our pieces are currently threatening
		int numPiecesWeAreThreatening = OffensiveHeuristics.getNumberOfPiecesMaxPlayerIsThreatening(node);

		return damageDealtInThisNode + numPiecesWeAreThreatening;
	}

	public static double getDefensiveMaxPlayerHeuristicValue(DFSTreeNode node)
	{
		// how many pieces exist on our team?
		int numPiecesAlive = DefensiveHeuristics.getNumberOfMaxPlayersAlivePieces(node);

		// what is the state of the pieces next to the king? add up the values of the neighboring pieces
		// positive value for friendly pieces and negative value for enemy pieces (will clamp at 0)
		int kingSurroundingPiecesValueTotal = DefensiveHeuristics.getClampedPieceValueTotalSurroundingMaxPlayersKing(node);

		// how many pieces are threatening us?
		int numPiecesThreateningUs = DefensiveHeuristics.getNumberOfPiecesThreateningMaxPlayer(node);

		return numPiecesAlive + kingSurroundingPiecesValueTotal + numPiecesThreateningUs;
	}


 




	public static double getMaxPlayerHeuristicValue(DFSTreeNode node)
	{
		// please replace this!
		return DefaultHeuristics.getMaxPlayerHeuristicValue(node);
	}

}
