package camp;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

public class CAMPSettlement {
	
	public int s_ID;
	public double population;
	public double m_domesticVisits;
	public double a_domesticVisits;
	public double m_travels;
	public double a_travels;
	public double m_localContactRate;
	public double a_localContactRate;
	public double m_travelContactRate;
	public double a_travelContactRate;
	public double builtUp;
	public double naturalQuality;
	public double HU_NaturalQuality;
	public double potentialAttractiveness;
	public double localPotentialAttractiveness;
	
	//public Coordinate coord; 
	public int x = 0;
	public int y = 0;
	
	public List<CAMPCA> s_cells;
	
	//@ScheduledMethod(start=12, interval=12, priority=2)
	@ScheduledMethod(start=240, interval=1, priority=2)
	public void step(){
	
		this.a_localContactRate = this.a_localContactRate + this.m_localContactRate;	
		this.m_localContactRate = 0;
		this.a_travelContactRate = this.a_travelContactRate + this.m_travelContactRate;	
		this.m_travelContactRate = 0;
		this.a_domesticVisits = this.a_domesticVisits + this.m_domesticVisits;
		this.m_domesticVisits = 0;
		this.a_travels = this.a_travels + this.m_travels;
		this.m_travels = 0;
		
		int monthinyear = ((int) RepastEssentials.GetTickCount())%12;
		
		if (monthinyear == 0){
			this.a_localContactRate = 0;
			this.a_travelContactRate = 0;
			this.a_domesticVisits = 0;
			this.a_travels = 0;
		}
		
		samedayVisit();
		overnightTrip();
		
		//this.a_contactRate = this.a_contactRate + this.m_contactRate;	
		//System.out.println(this.m_contactRate);
			
	}
	
	// 1.9% (1.5% potential)  3 hrs daily (1-300 visits per month person)
	// Av = Size*Quality^2/sqrt(Dis)
	public void samedayVisit(){	
		
		//System.out.println(">> settlement ID: " + this.s_ID + " is sending same-day visits");
		
		Context<Object> context = ContextUtils.getContext(this);
		Grid<Object> grid = (Grid)context.getProjection("Grid");
			
		for(int i=0; i< this.s_cells.size(); i++){
			
			CAMPCA ccc = new CAMPCA();
			ccc = this.s_cells.get(i);
			
			if(ccc.state.residents > 0) {
				
				Random rr = new Random();
				
				double activeVisitor = ccc.state.residents * 0.015;
				double cell_visits = ((int)(Math.abs(rr.nextGaussian() * 15)) + 10) *30;	
				//double cell_visits = 15 * 365;	
				double totoVisit = activeVisitor * cell_visits;
				
				//System.out.println(ccc.state.natural_quality);
				
				double totoAv =0;
				
				GridCellNgh <Object> nghCreator = new GridCellNgh<Object>(grid, grid.getLocation(ccc), Object.class, 2, 2);
				List <GridCell<Object>> gridCells = nghCreator.getNeighborhood(false);
				SimUtilities.shuffle(gridCells, RandomHelper.getUniform());	
				
				for (GridCell<Object>cell:gridCells) {
					
					HmWalking: {
						if (grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY()) == null) break HmWalking;
						else if (grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY()).getClass().equals(CAMPCA.class)) {
							
							CAMPCA neighbor = (CAMPCA) grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY());
							
							double dis = Math.sqrt(Math.pow((neighbor.state.n_x - ccc.state.n_x),2) + Math.pow((neighbor.state.n_y - ccc.state.n_y),2)) * 1000 + 1;				
							double av = Math.pow(neighbor.state.natural_quality, 2)/Math.sqrt(dis);
							
							//System.out.println("dis:"+ dis +";"+"av:"+av);
							
							neighbor.state.potential_attractiveness = av;						
							totoAv = totoAv + av;
							
						}
						else break HmWalking; 
						
					}
				}
				
				//System.out.println("totoAv:"+ totoAv +";"+"totoVisit:"+totoVisit);
				
				double totoContRate = 0;
				
				for (GridCell<Object>cell:gridCells) {
				
					HmWalking: {
						if (grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY()) == null) break HmWalking;
						else if (grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY()).getClass().equals(CAMPCA.class)) {
							
							CAMPCA neighbor = new CAMPCA();	
							neighbor = (CAMPCA) grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY());
							
							double vvv = totoVisit * neighbor.state.potential_attractiveness/totoAv;
							double ctt = neighbor.state.DqIN * 0.0012 * vvv * 0.5 * 0.08;
							
							neighbor.state.m_visits = neighbor.state.m_visits + vvv;							
							neighbor.state.m_contactRate_tick_habitat = neighbor.state.m_contactRate_tick_habitat + ctt;
							
							//System.out.println("neighbor.state.m_visits:"+ neighbor.state.m_visits +";"+"neighbor.state.m_contactRate:"+neighbor.state.m_contactRate_tick_habitat);
							
							totoContRate = ctt + totoContRate;
							
						}
						else break HmWalking; 
						
					}
				}
				
				ccc.state.m_contactRate_residential = totoContRate + ccc.state.m_contactRate_residential;
				
				this.m_localContactRate = totoContRate + this.m_localContactRate;
				
				//System.out.println("totoVisit:"+ totoVisit +";"+"totoContRate:"+totoContRate);				
			}		

		}
				
	}
	
	public void overnightTrip(){
		
		double toto_ovntTrips = estimateOvernightTrips();
		
		this.m_travels = toto_ovntTrips;
		
		//System.out.println(">> settlement ID: " + this.s_ID + " is sending ~" + toto_ovntTrips + " over-night visits");
		
		double toto_av = 0;
		
		Iterator<Object> sttls0 = CAMPContextBuilder.mainContext.getObjects(CAMPSettlement.class).iterator();
		
		while(sttls0.hasNext()) {
    		
    		CAMPSettlement sss = (CAMPSettlement)(sttls0.next());	
    		
			double dis = Math.sqrt(Math.pow((this.x - sss.x),2) + Math.pow((this.y - sss.y),2)) * 1000 + 1;				
			double av = Math.pow(sss.localPotentialAttractiveness, 2)/Math.sqrt(dis);
			sss.potentialAttractiveness = av;
			toto_av = toto_av + av;
    	}
		
		double totoContRate = 0;
		
		Iterator<Object> sttls1 = CAMPContextBuilder.mainContext.getObjects(CAMPSettlement.class).iterator();
		
		while(sttls1.hasNext()) {
    		
    		CAMPSettlement sss = (CAMPSettlement)(sttls1.next());	    		
			double dvv = toto_ovntTrips * sss.potentialAttractiveness / toto_av;
			//if (sss.m_domesticVisits > 1) System.out.println("sss.m_domesticVisits:"+ sss.m_domesticVisits);
			
			for(int i =0; i<sss.s_cells.size(); i++){
								
				double vvv = dvv * sss.s_cells.get(i).state.local_potential_attractiveness/sss.localPotentialAttractiveness;
				double ctt = sss.s_cells.get(i).state.DqIN * 0.0012 * vvv * 0.15;
				//System.out.println("sss.s_cells.get(i).state.local_potential_attractiveness:"+ sss.s_cells.get(i).state.local_potential_attractiveness +";"+"sss.localPotentialAttractiveness:"+sss.localPotentialAttractiveness);
				
				sss.s_cells.get(i).state.m_visits = sss.s_cells.get(i).state.m_visits  + vvv;
				sss.s_cells.get(i).state.m_contactRate_tick_habitat = sss.s_cells.get(i).state.m_contactRate_tick_habitat + ctt;
				
				sss.m_domesticVisits = sss.m_domesticVisits + dvv;
				
				//System.out.println("vvv:"+ vvv +";"+"ctt:"+ctt);
				
				totoContRate = ctt + totoContRate;
			
			}
			
			this.m_travelContactRate = totoContRate + this.m_travelContactRate;
    	}
		
		//System.out.println("totoVisit:"+ toto_ovntTrips +";"+"totoContRate:"+totoContRate)		
		
	}
	
	public void initialiseCells() {
		this.s_cells = new ArrayList<CAMPCA>();
		//this.f_visitors = new ArrayList<Double>();
	}
	
	public void initialiseSettlement(){
		
		double bb = 0;
		double pp = 0;
		double qq = 0;
		double aa = 0;
		
		for (int i =0; i<this.s_cells.size(); i++){
		
			bb = bb + this.s_cells.get(i).state.p_res +this.s_cells.get(i).state.p_gre;
			pp = pp + this.s_cells.get(i).state.residents;
			qq = qq + this.s_cells.get(i).state.natural_quality;
			
			double dis = Math.sqrt(Math.pow((this.x - this.s_cells.get(i).state.n_x),2) + Math.pow((this.y - this.s_cells.get(i).state.n_y),2)) * 1000 + 1;				
			double cell_aa = Math.pow(this.s_cells.get(i).state.natural_quality, 2)/Math.sqrt(dis);
			
			this.s_cells.get(i).state.local_potential_attractiveness = cell_aa;
			aa = aa + cell_aa;
			//System.out.println(zz);
		}
		this.builtUp = bb;
		this.population = pp;
		this.naturalQuality = qq;
		this.localPotentialAttractiveness = aa;
		
		//System.out.println("this.naturalQuality:"+ this.naturalQuality +";"+"this.localPotentialAttractiveness:"+this.localPotentialAttractiveness);
		//this.naturalQuality = aa;
	}
	
	public void calculateXY(){
		
		int cc = 0;
		double xx =0, yy=0;
		
		for (int i =0; i<this.s_cells.size(); i++){
			
			if(this.builtUp > 0){

				double zz = this.s_cells.get(i).state.p_res+this.s_cells.get(i).state.p_gre;
				//System.out.println(zz);
				if (zz>0){
					xx = xx + this.s_cells.get(i).state.n_x;
					yy = yy + this.s_cells.get(i).state.n_y;		
					cc ++;
				}
			}
			else{		
				xx = xx + this.s_cells.get(i).state.n_x;
				yy = yy + this.s_cells.get(i).state.n_y;		
				cc ++;
			}
		}
		
		this.x = (int) (xx/cc);
		this.y = (int) (yy/cc);
		
		if(!((this.x + this.x)>0)){
			System.out.println(">> settlement ID: " + this.s_ID + " has a size of " + this.s_cells.size() + ", centred @ x:" + this.x + " ; y: " + this.y);
		}
	}
	
	public void updatePopulation(){
		
	}
	
	public double getPopulation() {
		return this.population;
	}
	
	public double getContactRate() {
		return this.m_localContactRate + this.m_travelContactRate;
	}
	
	public double getContactLgSize() {		
		double lg_size = Math.log(this.m_localContactRate + this.m_travelContactRate);		
		if (lg_size <0) lg_size = 0;		
		return lg_size;
	}
	
	public double getLocalContactRate() {
		return this.m_localContactRate;
	}
	
	public double getLocalContactLgSize() {		
		double lg_size = Math.log(this.m_localContactRate);		
		if (lg_size <0) lg_size = 0;		
		return lg_size;
	}

	public double getTravelContactRate() {
		return this.m_travelContactRate;
	}
	
	public double getTravelContactLgSize() {		
		double lg_size = Math.log(this.m_travelContactRate);		
		if (lg_size <0) lg_size = 0;		
		return lg_size;
	}

	public double getDomesticVisits() {
		return this.m_domesticVisits;
	}
	
	public double getDomesticTravels() {				
		return this.m_travels;
	}

	
	public double estimateOvernightTrips() {
		
		double p_1to3 = 0, p_4plus = 0;
		double p_1to3_leisure = 0.3293, p_4plus_leisure = 0.6131;
		double p_outdoor_leisure = 0.0423;
		double pop = this.population;
		double totoTrips = 0;
		
		if ((this.s_ID >= 393) && (this.s_ID <= 415)){
			p_1to3 = 0.332; p_4plus = 0.282;
		}
		else{
			if (pop < 2000) {p_1to3 = 0.195; p_4plus = 0.107;}
			else if (pop < 5000) {p_1to3 = 0.172; p_4plus = 0.119;}
			else if (pop < 10000) {p_1to3 = 0.195; p_4plus = 0.128;}
			else if (pop < 50000) {p_1to3 = 0.298; p_4plus = 0.189;}
			else if (pop < 100000) {p_1to3 = 0.310; p_4plus = 0.211;}
			else {p_1to3 = 0.310; p_4plus = 0.282;}
		}

		Random rn = new Random();
		
		//totoTrips = this.population * ((p_1to3/9) * (rn.nextInt(3) + 1) * p_1to3_leisure + (p_4plus/9) * (rn.nextInt(4) + 4) * p_4plus_leisure) * p_outdoor_leisure;
		totoTrips = this.population * ((p_1to3/9) * (rn.nextInt(3) + 1) * p_1to3_leisure + (p_4plus/9) * (rn.nextInt(4) + 4) * p_4plus_leisure) * p_outdoor_leisure;
		
		return totoTrips;
	
	}
		
}
