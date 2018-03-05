package d1team;

public class Meco{

	public String nome;
	public double x;
	public double y;
	public double dirAngulo;
	public double distancia;
	public int jaVisitado;


	public Meco(String nome, double x, double y, double dirAngulo, double distancia){
		this.nome = nome;
		this.x = x;
		this.y = y;
		this.dirAngulo = dirAngulo;
		this.distancia = distancia;
		jaVisitado=0;
	}

}