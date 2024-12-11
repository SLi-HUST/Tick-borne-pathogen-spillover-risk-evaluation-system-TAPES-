package camp;

public class CellStates {
	
	public double cell_ID = 0;
	public int region_ID = 0;
	public int n_x = 0;
	public int n_y = 0;

	public double totoLarvaTick = 0;
	public double totoNymphTick = 0;
	public double totoAdultTick = 0;
	public double totoReserviorHost = 0 ;
	public double totoReproductionHost = 0;
	public double totoLivestockHost = 0 ;
	
	//public double i_totoReproductionHost = 0;	
	//public double i_totoReserviorHost = 0;
	//public double i_infectiousReserviorHost = 0;
	//public double i_totoLivestockHost = 0;
	
	public double infectiousLarvaTick = 0;
	public double infectiousNymphTick = 0;
	public double infectiousAdultTick = 0;
	public double infectiousReserviorHost = 0;
	
	public double DqIN = 0;
	
	public double[] temperature = new double[12];
	
	public double d_totoEgg = 0;
	public double d_totoLarvaTick = 0;
	public double d_totoNymphTick = 0;
	public double d_totoAdultTick = 0;	
	public double d_infectiousEgg = 0;
	public double d_infectiousLarvaTick = 0;
	public double d_infectiousNymphTick = 0;
	public double d_infectiousAdultTick = 0;
	
	public double dp_d_totoEgg = 0;
	//public double dp_d_totoLarvaTick = 0;
	public double dp_d_totoNymphTick = 0;
	public double dp_d_totoAdultTick = 0;	
	public double dp_d_infectiousEgg = 0;
	//public double dp_d_infectiousLarvaTick = 0;
	public double dp_d_infectiousNymphTick = 0;
	public double dp_d_infectiousAdultTick = 0;
	
	public double landscape = 0;
	public double elevation = 0;
	public double slope = 0;
	//public double d_T = 0;
	public double deerSuitability = 0;
	public double tickHabitatScalingFactor = 0;
	public double resvSuitability = 0;
	
	public double residents = 0;
	public double m_visits = 0;
	public double a_visits = 0;
	public double m_contactRate_tick_habitat = 0;
	public double a_contactRate_tick_habitat = 0;
	public double m_contactRate_residential = 0;
	public double a_contactRate_residential = 0;	
	public double natural_quality = 0;
	public double serivce_score = 0;
	public double potential_attractiveness = 0;
	public double local_potential_attractiveness = 0;
	public double p_res = 0;
	public double p_com = 0;
	public double p_gre = 0;
	public double d2lake = 0;
	public double d2river = 0;
	public double d2capital = 0;
	public double d2town = 0;
	public double d2village = 0;
	
}
