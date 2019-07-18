package enums;

public enum GameState {
	STARTING(Messages.getString("GameState.KickMessage.Starting"), false, false, false, false, 0, null, //$NON-NLS-1$
			Messages.getString("GameState.MOTDRestarting")), //$NON-NLS-1$
	WAITING_FOR_PLAYERS(null, true, true, false, false, 30, null, Messages.getString("GameState.MOTDLobby")), //$NON-NLS-1$
	IN_PROGRESS_SPAWNED(Messages.getString("GameState.KickMessage.InProgress"), false, false, false, false, 10, //$NON-NLS-1$
			String.format(Messages.getString("GameState.Broadcast.StartsIn"), 10), //$NON-NLS-1$
			Messages.getString("GameState.MOTDProgressSpawned")), //$NON-NLS-1$
	IN_PROGRESS_GRACE_PERIOD(Messages.getString("GameState.KickMessage.InProgress"), false, true, false, true, 30, //$NON-NLS-1$
			String.format(Messages.getString("GameState.Broadcast.Started"), 30), //$NON-NLS-1$
			Messages.getString("GameState.MOTDProgressGrace")), //$NON-NLS-1$
	IN_PROGRESS_PVP(Messages.getString("GameState.KickMessage.InProgress"), false, true, true, true, 600, //$NON-NLS-1$
			Messages.getString("GameState.Broadcast.GraceEnd"), Messages.getString("GameState.MOTDProgressPvP")), //$NON-NLS-1$ //$NON-NLS-2$
	WAITING_FOR_RESTART(Messages.getString("GameState.KickMessage.Restart"), false, true, false, false, 10, null, //$NON-NLS-1$
			Messages.getString("GameState.MOTDRestarting")); //$NON-NLS-1$

	public final String kickMessage;
	public final boolean joinable;
	public final boolean canMove;
	public final boolean canReceiveDamage;
	public final boolean canBuild;
	public final int delay;
	public final String broadcast;
	public final String motd;

	private GameState(String kickMessage, boolean joinable, boolean canMove, boolean canReceiveDamage, boolean canBuild,
			int delay, String broadcast, String motd) {
		this.kickMessage = kickMessage;
		this.joinable = joinable;
		this.canMove = canMove;
		this.canReceiveDamage = canReceiveDamage;
		this.canBuild = canBuild;
		this.delay = delay;
		this.broadcast = broadcast;
		this.motd = motd;
	}
}
