package GJNeural;

public abstract class TrainUpdater{
	public int update;
	public TrainUpdater(int update){
		this.update = update;
	}
	public abstract void Update();
}
