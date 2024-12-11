package camp;

//import java.util.Iterator;
import java.util.List;
import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
//import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.random.RandomHelper;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;
import repast.simphony.valueLayer.GridValueLayer;

public class CAMPCA {
	
	public CellStates state;
	public CellStates oldState;
	
	Parameters parm = RunEnvironment.getInstance().getParameters();
	int monthCount = 0;

	//parameters for tick
	double adEggRate = (Double)parm.getValue("adEggRate");
	
	double survival_questing_La = (Double)parm.getValue("survivalRatequestingLa");
	double survival_questing_Ny = (Double)parm.getValue("survivalRatequestingNy");
	double survival_questing_Ad = (Double)parm.getValue("survivalRatequestingAd");
	
	double survival_feeding_La = (Double)parm.getValue("survivalRatefeedingLa");
	double survival_feeding_Ny = (Double)parm.getValue("survivalRatefeedingNy");
	double survival_feeding_Ad = (Double)parm.getValue("survivalRatefeedingAd");
	
	double survival_developing_Eg_La = (Double)parm.getValue("survivalRatedevEgtoLa");
	double survival_developing_La_Ny = (Double)parm.getValue("survivalRatedevLatoNy");
	double survival_developing_Ny_Ad  = (Double)parm.getValue("survivalRatedevNytoAd");
	double survival_developing_Ad_Eg  = (Double)parm.getValue("survivalRatedevAdtoEg");
	
	double develop_Eg_La = (Double)parm.getValue("developmentRateEgtoLa");
	double develop_La_Ny = (Double)parm.getValue("developmentRateLatoNy");
	double develop_Ny_Ad  = (Double)parm.getValue("developmentRateNytoAd");
	double develop_Ad_Eg  = (Double)parm.getValue("developmentRateAdtoEg");

	double mmxLaOnRs = (Double)parm.getValue("mmxLaonReserv");
	double mmxNyOnRs = (Double)parm.getValue("mmxNyonReserv");
	double mmxAdOnRs = (Double)parm.getValue("mmxAdonReserv");
	
	double mmxLaOnRp = (Double)parm.getValue("mmxLaonRepro");	
	double mmxNyOnRp = (Double)parm.getValue("mmxNyonRepro");
	double mmxAdOnRp = (Double)parm.getValue("mmxAdonRepro");	
	
	double rate_LaOnRs = (Double)parm.getValue("hostfindingRateLaonReserv");
	double rate_NyOnRs = (Double)parm.getValue("hostfindingRateNyonReserv");
	double rate_AdOnRs = (Double)parm.getValue("hostfindingRateAdonReserv");
	
	double rate_LaOnRp = (Double)parm.getValue("hostfindingRateLaonRepro");	
	double rate_NyOnRp = (Double)parm.getValue("hostfindingRateNyonRepro");
	double rate_AdOnRp = (Double)parm.getValue("hostfindingRateAdonRepro");	
	
	//double f_questing = 1;
	double p_diapause = 0;
	double f_questing = 0;
	
	//parameters for transmission
	double effHosttoTick = (Double)parm.getValue("effHosttoTick");	
	double effTicktoHost = (Double)parm.getValue("effTicktoHost");	
	double effTicktoEgg = (Double)parm.getValue("effTicktoEgg");	
	
	//host density
	double capacityRepWood = (Double)parm.getValue("capacityRepWood");
	double capacityLivGrass = (Double)parm.getValue("capacityLivGrass");
	double birthRes = (Double)parm.getValue("birthRes");
	double mortalityRes = (Double)parm.getValue("mortalityRes");
	
	//host movement
	double MCReservior = (Double)parm.getValue("mCReservior"); 
	double MCLivestock = (Double)parm.getValue("mCLivestock"); 
	double MCReproduction = (Double)parm.getValue("mCReproduction"); 
	double MCReproductionDis = (Double)parm.getValue("mCReproductionDis");
	double propDisRep = (Double)parm.getValue("propDisRep"); 

	//parameters for landscape
	double scallingfactor = (Double)parm.getValue("scallingfactor");
	double propinGland = (Double)parm.getValue("propinGland");
	
	double TT = 0;
	
	/**
	 * Maintain the history of the state for one time interval
	 */
	
	@ScheduledMethod(start=0, interval=1, priority=5)
	public void setOldState(){							
		oldState = state;
	}
	
	@ScheduledMethod(start=0, interval=1, priority=4)
	public void step(){
		
		//System.out.println("tick survial");
		
		CellStates ccc = new CellStates();	
		CellStates ooo = new CellStates();	
		
		ooo = this.oldState;		
		ccc.cell_ID = ooo.cell_ID;
		ccc.region_ID = ooo.region_ID;
		ccc.temperature = ooo.temperature;
		ccc.landscape = ooo.landscape;
		ccc.elevation = ooo.elevation;
		//ccc.d_T = ooo.d_T;
		ccc.deerSuitability = ooo.deerSuitability;
		ccc.resvSuitability = ooo.resvSuitability;
		ccc.tickHabitatScalingFactor = ooo.tickHabitatScalingFactor;
		
		ccc.n_x = ooo.n_x;	
		ccc.n_y = ooo.n_y;	
		ccc.residents = ooo.residents;	
		ccc.m_visits = ooo.m_visits;
		ccc.a_visits = ooo.a_visits;
		ccc.natural_quality = ooo.natural_quality;
		ccc.serivce_score = ooo.serivce_score;
		ccc.potential_attractiveness = ooo.potential_attractiveness;
		ccc.local_potential_attractiveness = ooo.local_potential_attractiveness;
		ccc.m_contactRate_tick_habitat = 0;
		ccc.a_contactRate_tick_habitat = ooo.a_contactRate_tick_habitat + ooo.m_contactRate_tick_habitat;
		ccc.m_contactRate_residential = 0;
		ccc.a_contactRate_residential = ooo.a_contactRate_residential + ooo.m_contactRate_residential;
		
		ccc.DqIN = ooo.DqIN;
		
		int monthinyear = monthCount%12;
		
		double xfac = 0;
		
		/*
		if (monthinyear>47 ||monthinyear<9) {
			//System.out.println("winter month:" + monthinyear);
			xfac = 3;
		}*/
		
		//TT = ccc.temperature[monthinyear] + ccc.d_T + xfac;
		//System.out.println("winter month:" + monthinyear + "=== temp:" + TT );
		
		TT = ccc.temperature[monthinyear];
		
		develop_Ad_Eg = update_develop_Ad_Eg(TT);
		develop_Eg_La = update_develop_Eg_La(TT);
		develop_La_Ny = update_develop_La_Ny(TT);
		develop_Ny_Ad = update_develop_Ny_Ad(TT);
		
		//System.out.println("birthRes = " + birthRes);
		//System.out.println("capacityResGrass = " + capacityResGrass);
		//System.out.println("mortalityRes = " + mortalityRes);
		
		ccc.totoReserviorHost = ooo.totoReserviorHost;		
		ccc.infectiousReserviorHost = ooo.infectiousReserviorHost;
		ccc.totoReproductionHost = ooo.totoReproductionHost;
		ccc.totoLivestockHost = ooo.totoLivestockHost;
		
		//setting initial patterns of hosts
		/*
		 * if(monthCount == 0){
			ccc.i_totoReserviorHost = ooo.totoReserviorHost;		
			ccc.i_infectiousReserviorHost = ooo.infectiousReserviorHost;
			ccc.i_totoReproductionHost = ooo.totoReproductionHost;
			ccc.i_totoLivestockHost = ooo.totoLivestockHost;
		}else{
			ccc.i_totoReserviorHost = ooo.i_totoReserviorHost;		
			ccc.i_infectiousReserviorHost = ooo.i_infectiousReserviorHost;
			ccc.i_totoReproductionHost = ooo.i_totoReproductionHost;
			ccc.i_totoLivestockHost = ooo.i_totoLivestockHost;
		}
		*/
		birthRes = update_birthRes(monthinyear);
		double capacityRes = ccc.resvSuitability * update_capacityRes(monthCount) * 100;		
		mortalityRes = update_mortalityRes(capacityRes) * ccc.totoReserviorHost;		
		ccc.totoReserviorHost = ccc.totoReserviorHost * (1 + birthRes/12 - mortalityRes/12);
		if (monthCount> 23) ccc.infectiousReserviorHost = ccc.infectiousReserviorHost * (1 - mortalityRes/12);
		
		//on-setting ticks undergoing diapasue 
		if (monthinyear == 0){
			
			ooo.d_totoEgg = ooo.d_totoEgg + ooo.dp_d_totoEgg;
			ooo.d_totoNymphTick = ooo.d_totoNymphTick + ooo.dp_d_totoNymphTick;
			ooo.d_totoAdultTick = ooo.d_totoAdultTick + ooo.dp_d_totoAdultTick;
			ooo.d_infectiousEgg = ooo.d_infectiousEgg + ooo.dp_d_infectiousEgg;
			ooo.d_infectiousNymphTick = ooo.d_infectiousNymphTick + ooo.dp_d_infectiousNymphTick;
			ooo.d_infectiousAdultTick = ooo.d_infectiousAdultTick + ooo.dp_d_infectiousAdultTick;

			ooo.dp_d_totoEgg = 0;
			ooo.dp_d_totoNymphTick = 0;
			ooo.dp_d_totoAdultTick = 0;
			ooo.dp_d_infectiousEgg = 0;
			ooo.dp_d_infectiousNymphTick = 0;
			ooo.dp_d_infectiousAdultTick = 0;
			
			ccc.a_contactRate_tick_habitat = 0;
			ccc.a_contactRate_residential = 0;
			
			//ccc.totoReserviorHost = ccc.i_totoReserviorHost;		
			//ccc.infectiousReserviorHost = ccc.i_infectiousReserviorHost;
			//ccc.totoReproductionHost = ccc.i_totoReproductionHost;
			//ccc.totoLivestockHost = ccc.i_totoLivestockHost;
		}
		
		double sfactor = ccc.tickHabitatScalingFactor;
		//System.out.println(sfactor);
		
		ccc.d_totoEgg = ooo.d_totoEgg * (1 - (1 - survival_developing_Ad_Eg) * sfactor - develop_Ad_Eg);
		ccc.d_totoLarvaTick = (ooo.d_totoEgg * develop_Ad_Eg + ooo.d_totoLarvaTick) * (1 - (1 - survival_developing_Eg_La) * sfactor - develop_Eg_La);
		ccc.d_totoNymphTick = ooo.d_totoNymphTick * (1 - (1 - survival_developing_La_Ny) * sfactor - develop_La_Ny);
		ccc.d_totoAdultTick = ooo.d_totoAdultTick * (1 - (1 - survival_developing_Ny_Ad) * sfactor - develop_Ny_Ad);
		
		ccc.d_infectiousEgg = ooo.d_infectiousEgg * (1 - (1 - survival_developing_Ad_Eg) * sfactor - develop_Ad_Eg) ;
		ccc.d_infectiousLarvaTick = (ooo.d_infectiousEgg * develop_Ad_Eg + ooo.d_infectiousLarvaTick) * (1 - (1 - survival_developing_Eg_La) * sfactor - develop_Eg_La) ;
		ccc.d_infectiousNymphTick = ooo.d_infectiousNymphTick * (1 - (1 - survival_developing_La_Ny) * sfactor - develop_La_Ny);
		ccc.d_infectiousAdultTick = ooo.d_infectiousAdultTick * (1 - (1 - survival_developing_Ny_Ad) * sfactor - develop_Ny_Ad);

		ccc.totoLarvaTick = (ooo.totoLarvaTick + ooo.d_totoLarvaTick * develop_Eg_La) * (1 - (1 - survival_questing_La) * sfactor); //adEggRate * Math.pow(1 - (1 - survival_developing_Ad_La)* sfactor, tAL)) * (1 - (1 - survival_questing_La) * sfactor);
		ccc.totoNymphTick = (ooo.totoNymphTick + ooo.d_totoNymphTick * develop_La_Ny) * (1 - (1 - survival_questing_Ny) * sfactor); //Math.pow(1 - (1 - survival_developing_La_Ny)* sfactor, tLN)) * (1 - (1 - survival_questing_Ny) * sfactor);
		ccc.totoAdultTick = (ooo.totoAdultTick + ooo.d_totoAdultTick * develop_Ny_Ad) * (1 - (1 - survival_questing_Ad) * sfactor); //Math.pow(1 - (1 - survival_developing_Ny_Ad)* sfactor, tNA)) * (1 - (1 - survival_questing_Ad) * sfactor);

		ccc.infectiousLarvaTick = (ooo.infectiousLarvaTick + ooo.d_infectiousLarvaTick * develop_Eg_La) * (1 - (1 - survival_questing_La) * sfactor); //  * adEggRate * effTicktoEgg * Math.pow(1 - (1 - survival_developing_Ad_La)* sfactor, tAL)) * (1 - (1 - survival_questing_La) * sfactor);
		ccc.infectiousNymphTick = (ooo.infectiousNymphTick + ooo.d_infectiousNymphTick * develop_La_Ny) * (1 - (1 - survival_questing_Ny) * sfactor); // * Math.pow(1 - (1 - survival_developing_La_Ny)* sfactor, tLN)) * (1 - (1 - survival_questing_Ny) * sfactor);
		ccc.infectiousAdultTick = (ooo.infectiousAdultTick + ooo.d_infectiousAdultTick  * develop_Ny_Ad) * (1 - (1 - survival_questing_Ad) * sfactor); // * Math.pow(1 - (1 - survival_developing_Ny_Ad)* sfactor, tNA)) * (1 - (1 - survival_questing_Ad) * sfactor);
		
		ccc.dp_d_totoEgg = ooo.dp_d_totoEgg * (1 - (1 - survival_developing_Ad_Eg) * sfactor);
		ccc.dp_d_totoNymphTick = ooo.dp_d_totoNymphTick  * (1 - (1 - survival_developing_La_Ny) * sfactor);
		ccc.dp_d_totoAdultTick = ooo.dp_d_totoAdultTick * (1 - (1 - survival_developing_Ny_Ad) * sfactor);
		ccc.dp_d_infectiousEgg = ooo.dp_d_infectiousEgg * (1 - (1 - survival_developing_Ad_Eg) * sfactor);
		ccc.dp_d_infectiousNymphTick = ooo.dp_d_infectiousNymphTick * (1 - (1 - survival_developing_La_Ny) * sfactor);
		ccc.dp_d_infectiousAdultTick = ooo.dp_d_infectiousAdultTick * (1 - (1 - survival_developing_Ny_Ad) * sfactor);

		ccc.totoLarvaTick = checkValue(ccc.totoLarvaTick);
		ccc.totoNymphTick = checkValue(ccc.totoNymphTick);
		ccc.totoAdultTick = checkValue(ccc.totoAdultTick);
		ccc.infectiousLarvaTick = checkValue(ccc.infectiousLarvaTick);
		ccc.infectiousNymphTick = checkValue(ccc.infectiousNymphTick);
		ccc.infectiousAdultTick = checkValue(ccc.infectiousAdultTick);
		ccc.totoReserviorHost = checkValue(ccc.totoReserviorHost );
		ccc.infectiousReserviorHost = checkValue(ccc.infectiousReserviorHost);
		
		ccc.d_totoEgg = checkValue(ccc.d_totoEgg);
		ccc.d_totoLarvaTick = checkValue(ccc.d_totoLarvaTick);
		ccc.d_totoNymphTick = checkValue(ccc.d_totoNymphTick);
		ccc.d_totoAdultTick = checkValue(ccc.d_totoAdultTick);
		ccc.d_infectiousEgg = checkValue(ccc.d_infectiousEgg);
		ccc.d_infectiousLarvaTick = checkValue(ccc.d_infectiousLarvaTick);
		ccc.d_infectiousNymphTick = checkValue(ccc.d_infectiousNymphTick);
		ccc.d_infectiousAdultTick = checkValue(ccc.d_infectiousAdultTick);

		ccc.dp_d_totoEgg = checkValue(ccc.dp_d_totoEgg);
		ccc.dp_d_totoNymphTick = checkValue(ccc.dp_d_totoNymphTick);
		ccc.dp_d_totoAdultTick = checkValue(ccc.dp_d_totoAdultTick);
		ccc.dp_d_infectiousEgg = checkValue(ccc.dp_d_infectiousEgg);
		ccc.dp_d_infectiousNymphTick = checkValue(ccc.dp_d_infectiousNymphTick);
		ccc.dp_d_infectiousAdultTick = checkValue(ccc.dp_d_infectiousAdultTick);
		
		monthCount = monthCount+1;
		
		setState(ccc);	
		//state = ccc;
	}
	
	@ScheduledMethod(start=0, interval=1, priority=3)
	public void step1(){
		
		Context<Object> context = ContextUtils.getContext(this);
		Grid<Object> grid = (Grid)context.getProjection("Grid");
		
		//System.out.println("tick redistribution");
		
		CellStates ccc = new CellStates();
		ccc = this.state; 
		
		/**
		 * /////////////////////////////////////////////////////////////////////////////////////////////////
		 * ///////////////////////////////////////// tick picking-ups //////////////////////////////////////
		 * /////////////////////////////////////////////////////////////////////////////////////////////////
		 */  
	
		int monthinyear = monthCount %12;	
		//double TT = ccc.temperature[monthinyear];
			
		f_questing = update_f_questing(TT);
		//System.out.println(f_questing);
		//System.out.println("winter month:" + monthinyear + "=== temp:" + TT + "=== f_questing:" + f_questing );
		
		double avaliableLarvaTick = f_questing * ccc.totoLarvaTick;
		double avaliableNymphTick = f_questing * ccc.totoNymphTick;
		double avaliableAdultTick = f_questing * ccc.totoAdultTick * 0.5;
		
		ccc.DqIN = f_questing * ccc.infectiousNymphTick;
		
		double feeding_LaonReserv = rate_LaOnRs * avaliableLarvaTick;// * (1 - (rate_LaOnRs * avaliableLarvaTick / mmxLaOnRs * ccc.totoReserviorHost));//
		double feeding_NyonReserv = rate_NyOnRs * avaliableNymphTick;// * (1 - (rate_NyOnRs * avaliableNymphTick / mmxNyOnRs * ccc.totoReserviorHost));
		double feeding_AdonReserv = rate_AdOnRs * avaliableAdultTick;// * (1 - (rate_AdOnRs * avaliableAdultTick / mmxAdOnRs * ccc.totoReserviorHost));
		double feeding_LaonRepro = rate_LaOnRp * avaliableLarvaTick;// * (1 - (rate_LaOnRp * avaliableLarvaTick / mmxLaOnRp * (ccc.totoReproductionHost + ccc.totoLivestockHost)));// 0.063 * Math.pow(ccc.totoReserviorHost, 0.515);////oldState.totoTick;
		double feeding_NyonRepro = rate_NyOnRp * avaliableNymphTick;// * (1 - (rate_NyOnRp * avaliableNymphTick / mmxNyOnRp * (ccc.totoReproductionHost + ccc.totoLivestockHost)));//0.07 * Math.pow(ccc.totoReproductionHost, 0.515);//
		double feeding_AdonRepro  = rate_AdOnRp * avaliableAdultTick;// * (1 - (rate_AdOnRp * avaliableAdultTick / mmxAdOnRp * (ccc.totoReproductionHost + ccc.totoLivestockHost)));//0.42 * Math.pow(ccc.totoReproductionHost, 0.515); //  //oldState.totoTick;	
		
		if (feeding_LaonReserv > (mmxLaOnRs * ccc.totoReserviorHost)){
			feeding_LaonReserv = mmxLaOnRs * ccc.totoReserviorHost; 
			//System.out.println("month:" + monthCount + " ---maximum larva attachments on reservoir reached!!");
		} //else {feeding_LaonReserv = feeding_LaonReserv/mmxLaOnRs * ccc.totoReserviorHost; }
		if (feeding_NyonReserv > (mmxNyOnRs * ccc.totoReserviorHost)){
			feeding_NyonReserv = mmxNyOnRs * ccc.totoReserviorHost; 
			//System.out.println("month:" + monthCount + " ---maximum nymph attachments on reservoir reached!!");
		}
		if (feeding_AdonReserv > (mmxAdOnRs * ccc.totoReserviorHost)){
			feeding_AdonReserv = mmxAdOnRs * ccc.totoReserviorHost; 
			//System.out.println("month:" + monthCount + " ---maximum adult attachments on reservoir reached!!");
		}
		if (feeding_LaonRepro > (mmxLaOnRp * (ccc.totoReproductionHost + ccc.totoLivestockHost))){
			
			double d_LaonRepro = feeding_LaonRepro - mmxLaOnRp * (ccc.totoReproductionHost + ccc.totoLivestockHost);
			
			if (d_LaonRepro < (mmxLaOnRs * ccc.totoReserviorHost - feeding_LaonReserv)){
				feeding_LaonReserv = feeding_LaonReserv + d_LaonRepro;
			}
			else {feeding_LaonReserv = mmxLaOnRs * ccc.totoReserviorHost;}
			
			feeding_LaonRepro = mmxLaOnRp * (ccc.totoReproductionHost + ccc.totoLivestockHost); 
			//System.out.println("month:" + monthCount + " ---maximum larva attachments on reproduction reached!!");
		}
		if (feeding_NyonRepro > (mmxNyOnRp * (ccc.totoReproductionHost + ccc.totoLivestockHost))){
			
			double d_NyonRepro = feeding_NyonRepro - mmxNyOnRp * (ccc.totoReproductionHost + ccc.totoLivestockHost);
			
			if (d_NyonRepro < (mmxNyOnRs * ccc.totoReserviorHost - feeding_NyonReserv)){
				feeding_NyonReserv = feeding_NyonReserv + d_NyonRepro;			
			}
			else {feeding_NyonReserv = mmxNyOnRs * ccc.totoReserviorHost;}
			
			feeding_NyonRepro = mmxNyOnRp * (ccc.totoReproductionHost + ccc.totoLivestockHost); 
			//System.out.println("month:" + monthCount + " ---maximum nymph attachments on reproduction reached!!");
		}
		if (feeding_AdonRepro > mmxAdOnRp * (ccc.totoReproductionHost + ccc.totoLivestockHost)){
			feeding_AdonRepro = mmxAdOnRp * (ccc.totoReproductionHost + ccc.totoLivestockHost); 
			//System.out.println("month:" + monthCount + " ---maximum adult attachments on reproduction reached!!");
		}
		
		boolean tt1 = false;
		boolean tt2 = false;
		boolean tt3 = false;		
		
		if ((feeding_LaonReserv + feeding_LaonRepro) > avaliableLarvaTick){tt1 = true;}
		if ((feeding_NyonReserv + feeding_NyonRepro) > avaliableNymphTick){tt2 = true;}
		if ((feeding_AdonReserv + feeding_AdonRepro) > avaliableAdultTick){tt3 = true;}	
		
		if (tt1){
			if(feeding_LaonReserv > avaliableLarvaTick){
				feeding_LaonReserv = avaliableLarvaTick;
				feeding_LaonRepro = 0;
			}
			else{
				feeding_LaonRepro = avaliableLarvaTick - feeding_LaonReserv;
			}			
		}
		
		if (tt2){
			double ppp = feeding_NyonReserv/(feeding_NyonReserv + feeding_NyonRepro);
			feeding_NyonReserv = ppp * avaliableNymphTick;
			feeding_NyonRepro = (1 - ppp) * avaliableNymphTick;					
		}
		
		if (tt3){
			if(feeding_AdonRepro > avaliableAdultTick){
				feeding_AdonRepro = avaliableAdultTick;
				feeding_AdonReserv = 0;
			}
			else{	
				feeding_AdonReserv = avaliableAdultTick - feeding_AdonRepro;
			}							
		}
		
		//update all temperature- and density- dependent variables
		//adEggRate = update_f_Eggs(ccc.totoReproductionHost + ccc.totoLivestockHost, feeding_AdonRepro) * 2000;
		//survival_feeding_La = update_survival_feeding_La(ccc.totoReserviorHost, feeding_LaonReserv);
		//survival_feeding_Ny = update_survival_feeding_Ny(ccc.totoReproductionHost + ccc.totoLivestockHost + ccc.totoReserviorHost, feeding_NyonRepro + feeding_NyonReserv);
		//survival_feeding_Ad = update_survival_feeding_Ad(ccc.totoReproductionHost + ccc.totoLivestockHost, feeding_AdonRepro);
		//System.out.println(feeding_LaonReserv);
		
		double effHosttoLavae = 0.88;
		double effHosttoNymphs = 0.88;
		double effHosttoAdults = 0.8;
		
		double effLavaetoHost = 0.96;
		double effNymphstoHost = 0.96;
		double effAdultstoHost = 0.9;
		/**
		 * /////////////////////////////////////////////////////////////////////////////////////////////////
		 * ////////////////////////////////////// disease transmission /////////////////////////////////////
		 * /////////////////////////////////////////////////////////////////////////////////////////////////
		 */ 
		
		double feeding_inf_LaonReserv = 0;//
		double feeding_inf_NyonReserv = 0;
		double feeding_inf_AdonReserv = 0;
		double feeding_inf_LaonRepro = 0;// 0.063 * Math.pow(ccc.totoReserviorHost, 0.515);////oldState.totoTick;
		double feeding_inf_NyonRepro = 0;//0.07 * Math.pow(ccc.totoReproductionHost, 0.515);//
		double feeding_inf_AdonRepro  = 0;//0.42 * Math.pow(ccc.totoReproductionHost, 0.515); //  //oldState.totoTick;
		
		double trans_inf_Res_La = 0;
		double trans_inf_Res_Ny = 0;
		double trans_inf_Res_Ad = 0;
		double trans_inf_feeding_La = 0;
		double trans_inf_feeding_Ny = 0;
		double trans_inf_feeding_Ad = 0;
		
		if (ccc.totoReserviorHost > 0 && ccc.totoLarvaTick > 0){ 
			feeding_inf_LaonReserv = checkValue(feeding_LaonReserv * (ccc.infectiousLarvaTick/ccc.totoLarvaTick));
			trans_inf_Res_La = effLavaetoHost * feeding_inf_LaonReserv * (ccc.totoReserviorHost - ccc.infectiousReserviorHost) / ccc.totoReserviorHost;
			trans_inf_feeding_La = effHosttoLavae * ccc.infectiousReserviorHost * (feeding_LaonReserv - feeding_inf_LaonReserv) / ccc.totoReserviorHost;
		}
		
		if (ccc.totoReserviorHost > 0 && ccc.totoNymphTick > 0){ 
			feeding_inf_NyonReserv = checkValue((feeding_NyonReserv * (ccc.infectiousNymphTick/ccc.totoNymphTick)));
			trans_inf_Res_Ny = effNymphstoHost * feeding_inf_NyonReserv * (ccc.totoReserviorHost - ccc.infectiousReserviorHost) / ccc.totoReserviorHost;
			trans_inf_feeding_Ny = effHosttoNymphs * ccc.infectiousReserviorHost * (feeding_NyonReserv - feeding_inf_NyonReserv) / ccc.totoReserviorHost;
		}
		
		if (ccc.totoReserviorHost > 0 && ccc.totoAdultTick > 0){ 
			feeding_inf_AdonReserv = checkValue(feeding_AdonReserv * (ccc.infectiousAdultTick/ccc.totoAdultTick));
			trans_inf_Res_Ad = effAdultstoHost * feeding_inf_AdonReserv * (ccc.totoReserviorHost - ccc.infectiousReserviorHost) / ccc.totoReserviorHost;
			trans_inf_feeding_Ad = effHosttoAdults * ccc.infectiousReserviorHost * (feeding_AdonReserv - feeding_inf_AdonReserv) / ccc.totoReserviorHost;
		}
		
		if (ccc.totoReproductionHost > 0 && ccc.totoLarvaTick > 0){ 
			feeding_inf_LaonRepro = feeding_LaonRepro * (ccc.infectiousLarvaTick/ccc.totoLarvaTick);
		}
		
		if (ccc.totoReproductionHost > 0 && ccc.totoNymphTick > 0){ 
			feeding_inf_NyonRepro = feeding_NyonRepro * (ccc.infectiousNymphTick/ccc.totoNymphTick);
		}
		
		if (ccc.totoReproductionHost > 0 && ccc.totoAdultTick > 0){ 
			feeding_inf_AdonRepro  = feeding_AdonRepro * (ccc.infectiousAdultTick/ccc.totoAdultTick);
		}
		
		trans_inf_Res_La  = checkValue(trans_inf_Res_La);
		trans_inf_Res_Ny = checkValue(trans_inf_Res_Ny);
		trans_inf_Res_Ad = checkValue(trans_inf_Res_Ad);
		trans_inf_feeding_La = checkValue(trans_inf_feeding_La);
		trans_inf_feeding_Ny = checkValue(trans_inf_feeding_Ny);
		trans_inf_feeding_Ad = checkValue(trans_inf_feeding_Ad);
				
		ccc.totoLarvaTick = ccc.totoLarvaTick - (feeding_LaonReserv + feeding_LaonRepro);
		ccc.totoNymphTick = ccc.totoNymphTick - (feeding_NyonReserv + feeding_NyonRepro);
		ccc.totoAdultTick = ccc.totoAdultTick - (feeding_AdonReserv + feeding_AdonRepro);
		ccc.infectiousLarvaTick = ccc.infectiousLarvaTick - (feeding_inf_LaonReserv + feeding_inf_LaonRepro);
		ccc.infectiousNymphTick = ccc.infectiousNymphTick - (feeding_inf_NyonReserv + feeding_inf_NyonRepro);
		ccc.infectiousAdultTick = ccc.infectiousAdultTick - (feeding_inf_AdonReserv + feeding_inf_AdonRepro);
		
		double survival_LaonReserv = checkValue(survival_feeding_La - 0.05 - 0.049 *Math.log((1.01+feeding_LaonReserv/10)/ccc.totoReserviorHost));
		double survival_NyonReserv = checkValue(survival_feeding_Ny - 0.05 - 0.049 *Math.log((1.01+feeding_NyonReserv/6)/ccc.totoReserviorHost));
		double survival_AdonReserv = checkValue(survival_feeding_Ad - 0.05 - 0.049 *Math.log((1.01+feeding_AdonReserv/5)/ccc.totoReserviorHost));
		double survival_LaonRepro = checkValue(survival_feeding_La - 0.049 *Math.log((1.01+feeding_LaonRepro/10)/ccc.totoReproductionHost));
		double survival_NyonRepro = checkValue(survival_feeding_Ny - 0.049 *Math.log((1.01+feeding_NyonRepro/6)/ccc.totoReproductionHost));	
		double survival_AdonRepro = checkValue(survival_feeding_Ad - 0.049 *Math.log((1.01+feeding_AdonRepro/5)/ccc.totoReproductionHost));
		
		//if (survival_LaonReserv > 1){survival_LaonReserv = 1; System.out.println("survival_LaonReserv = 1");}
		//if (survival_LaonRepro > 1) {survival_LaonRepro = 1; System.out.println("survival_LaonRepro = 1");}
		//if (survival_NyonReserv > 1) {survival_NyonReserv = 1; System.out.println("survival_NyonReserv = 1");}
		//if (survival_NyonRepro > 1) {survival_NyonRepro = 1; System.out.println("survival_NyonRepro = 1");}
		//if (survival_AdonReserv > 1) {survival_AdonReserv = 1; System.out.println("survival_AdonReserv = 1");}
		//if (survival_AdonRepro > 1) {survival_AdonRepro = 1; System.out.println("survival_AdonRepro = 1");}
		
		//System.out.println(survival_feeding_La +"=survival_LaonReserv=="+ survival_LaonReserv);
		
		feeding_LaonReserv = feeding_LaonReserv * survival_LaonReserv;
		feeding_LaonRepro = feeding_LaonRepro * survival_LaonRepro;
		feeding_NyonReserv = feeding_NyonReserv * survival_NyonReserv;
		feeding_NyonRepro = feeding_NyonRepro * survival_NyonRepro;
		feeding_AdonReserv = feeding_AdonReserv * survival_AdonReserv;
		feeding_AdonRepro = feeding_AdonRepro * survival_AdonRepro;
		
		feeding_inf_LaonReserv = (feeding_inf_LaonReserv + trans_inf_feeding_La) * survival_LaonReserv;		
		feeding_inf_NyonReserv = (feeding_inf_NyonReserv + trans_inf_feeding_Ny) * survival_NyonReserv;
		feeding_inf_AdonReserv = (feeding_inf_AdonReserv + trans_inf_feeding_Ad) * survival_AdonReserv;
		feeding_inf_LaonRepro = feeding_inf_LaonRepro * survival_LaonRepro;
		feeding_inf_NyonRepro = feeding_inf_NyonRepro * survival_NyonRepro;
		feeding_inf_AdonRepro = feeding_inf_AdonRepro * survival_AdonRepro;		
		
		ccc.infectiousReserviorHost = ccc.infectiousReserviorHost + trans_inf_Res_La + trans_inf_Res_Ny + trans_inf_Res_Ad;
		
		if (ccc.infectiousReserviorHost >ccc.totoReserviorHost) ccc.infectiousReserviorHost = ccc.totoReserviorHost;
				//trans_inf_Res_La * survival_LaonReserv + trans_inf_Res_Ny * (1 - 0.35 * (1 - survival_NyonReserv)) + trans_inf_Res_Ad * (1 - 0.2 * (1 - survival_AdonReserv));
		
		boolean diapause = false;
		//double p_diapause = 0;
		
		if (monthinyear >= 6){
			diapause = true;
			p_diapause = 0.9;
		}else p_diapause = 0;
		
		/**
		 * /////////////////////////////////////////////////////////////////////////////////////////////////
		 * ///////////////////////////////// adding spatial components ////////////////////////////////////
		 * ////////////////////////////////////////////////////////////////////////////////////////////////
		 */    

		boolean mm1 = false;
		boolean mm2 = false;
		boolean mm3 = false;		
		
		if (ccc.totoReserviorHost > 0){mm1 = true;}
		if (ccc.totoLivestockHost > 0){mm2 = true;}
		if (ccc.totoReproductionHost > 0){mm3 = true;}	
		
		///////////////////////////////////////////////////////////////////
		/////////////////// reservior host movement////////////////////////
		///////////////////////////////////////////////////////////////////
		
		if(mm1){
			
			boolean isHomeRangingRes = false;
			boolean isDispersiveRes = false;
				
			//////////////////////////////
			////////Home-ranging//////////
			//////////////////////////////
			if (isHomeRangingRes){
				
				double p_res_out = Math.pow((MCReservior/500),2);
				
				double Out_totoReserviorHost = ccc.totoReserviorHost * p_res_out/8;	
				double Out_infectiousReserviorHost = ccc.infectiousReserviorHost * p_res_out/8;	
				double Out_f_NyonRes = feeding_NyonReserv * p_res_out/8;	
				double Out_f_AdonRes = feeding_AdonReserv * p_res_out/8;
				double Out_f_inf_NyonRes = feeding_NyonReserv * p_res_out/8;	
				double Out_f_inf_AdonRes = feeding_AdonReserv * p_res_out/8;		
				
				GridCellNgh <Object> nghCreator = new GridCellNgh<Object>(grid, grid.getLocation (this), Object.class, 1, 1);
				List <GridCell<Object >> gridCells = nghCreator.getNeighborhood(false);
				SimUtilities.shuffle(gridCells, RandomHelper.getUniform());	
				
				// Get the states of the 8 neighbors
				for (GridCell<Object>cell:gridCells) {
					
					RsHomeRanging: {				
						CAMPCA neighbor = new CAMPCA();
						
						if (grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY()) == null) break RsHomeRanging;
						else if (grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY()).getClass().equals(CAMPCA.class)) {
							neighbor = (CAMPCA) grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY());
						}
						else break RsHomeRanging; 
						
						Random rr = new Random();	
						int rrr1 = (int)(Math.abs((rr.nextGaussian() * MCReservior)));
						if (rrr1 > MCReservior){
							
							boolean flag1 = false;
							double capacityRes = neighbor.state.resvSuitability * update_capacityRes(monthCount) * 100;	

							if ((neighbor.state.totoReserviorHost + Out_totoReserviorHost) < capacityRes) flag1 = true;
							boolean isResHabitat = isReservoirHabitat(neighbor.state.landscape);
											
							if (isResHabitat && flag1) {									
								
								//ccc.totoReserviorHost = ccc.totoReserviorHost - Out_totoReserviorHost;
								//ccc.infectiousReserviorHost = ccc.infectiousReserviorHost - Out_infectiousReserviorHost;	
								
								feeding_NyonReserv = feeding_NyonReserv - Out_f_NyonRes;
								feeding_AdonReserv = feeding_AdonReserv - Out_f_AdonRes;
								feeding_inf_NyonReserv = feeding_inf_NyonReserv - Out_f_inf_NyonRes;
								feeding_inf_AdonReserv = feeding_inf_AdonReserv - Out_f_inf_AdonRes;
								
								//neighbor.state.totoReserviorHost = neighbor.state.totoReserviorHost + Out_totoReserviorHost;		
								//neighbor.state.infectiousReserviorHost = neighbor.state.infectiousReserviorHost + Out_infectiousReserviorHost;
								
								neighbor.state.d_totoAdultTick = neighbor.state.d_totoAdultTick + Out_f_NyonRes * ( 1 - p_diapause);	
								neighbor.state.d_totoEgg = neighbor.state.d_totoEgg + Out_f_AdonRes * adEggRate * ( 1 - p_diapause);
								neighbor.state.d_infectiousAdultTick = neighbor.state.d_infectiousAdultTick + Out_f_inf_NyonRes * ( 1 - p_diapause);
								neighbor.state.d_infectiousEgg = neighbor.state.d_infectiousEgg + Out_f_inf_AdonRes * adEggRate * effTicktoEgg * ( 1 - p_diapause);
								
								if(diapause){
									neighbor.state.dp_d_totoAdultTick = neighbor.state.dp_d_totoAdultTick + Out_f_NyonRes * p_diapause;	
									neighbor.state.dp_d_totoEgg = neighbor.state.dp_d_totoEgg + Out_f_AdonRes * adEggRate * p_diapause;
									neighbor.state.dp_d_infectiousAdultTick = neighbor.state.dp_d_infectiousAdultTick + Out_f_inf_NyonRes * p_diapause;
									neighbor.state.dp_d_infectiousEgg = neighbor.state.dp_d_infectiousEgg + Out_f_inf_AdonRes * adEggRate * effTicktoEgg * p_diapause;
								}
							}							
						}
					}			
				}	
			}
			
			//////////////////////////////
			////////dispersive//////////
			//////////////////////////////
			
			//if(ccc.totoReserviorHost>6000) {isDispersiveRes = true;}
			
			//centreStates = ccc;
			if(isDispersiveRes){
				//System.out.println("reservoir disperse");
				double p_res_out = 0.25/12;
				
				double Out_totoReserviorHost = ccc.totoReserviorHost * p_res_out;	
				double Out_infectiousReserviorHost = ccc.infectiousReserviorHost * p_res_out;	
				double Out_f_NyonRes = feeding_NyonReserv * p_res_out;	
				double Out_f_AdonRes = feeding_AdonReserv * p_res_out;
				double Out_f_inf_NyonRes = feeding_NyonReserv * p_res_out;	
				double Out_f_inf_AdonRes = feeding_AdonReserv * p_res_out;		
				
				GridCellNgh <Object> nghCreator = new GridCellNgh<Object>(grid, grid.getLocation (this), Object.class, 1, 1);
				List <GridCell<Object >> gridCells = nghCreator.getNeighborhood(false);
				SimUtilities.shuffle(gridCells, RandomHelper.getUniform());				
				
				for (GridCell<Object> cell:gridCells) {
					
					RsDispersive: {	
						CAMPCA neighbor = new CAMPCA();
						if (grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY()) == null){
							//System.out.println("!!!!!!!!!!!!!!! null object");
						}
						else if (grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY()).getClass().equals(CAMPCA.class)) {
														
							neighbor = (CAMPCA) grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY());	
							boolean flag1 = false;
							double capacityRes = neighbor.state.resvSuitability * update_capacityRes(monthCount) * 100;	
							if ((neighbor.state.totoReserviorHost + Out_totoReserviorHost) < capacityRes) flag1 = true;
							boolean isResHabitat = isReservoirHabitat(neighbor.state.landscape);
							
							if (isResHabitat && flag1) {
								
								//ccc.totoReserviorHost = ccc.totoReserviorHost - Out_totoReserviorHost;
								//ccc.infectiousReserviorHost = ccc.infectiousReserviorHost - Out_infectiousReserviorHost;	
								feeding_NyonReserv = feeding_NyonReserv - Out_f_NyonRes;
								feeding_AdonReserv = feeding_AdonReserv - Out_f_AdonRes;
								feeding_inf_NyonReserv = feeding_inf_NyonReserv - Out_f_inf_NyonRes;
								feeding_inf_AdonReserv = feeding_inf_AdonReserv - Out_f_inf_AdonRes;							
								
								//neighbor.state.totoReserviorHost = neighbor.state.totoReserviorHost + Out_totoReserviorHost;		
								//neighbor.state.infectiousReserviorHost = neighbor.state.infectiousReserviorHost + Out_infectiousReserviorHost;							
								
								neighbor.state.d_totoAdultTick = neighbor.state.d_totoAdultTick + Out_f_NyonRes * ( 1 - p_diapause);	
								neighbor.state.d_totoEgg = neighbor.state.d_totoEgg + Out_f_AdonRes * adEggRate * ( 1 - p_diapause);
								neighbor.state.d_infectiousAdultTick = neighbor.state.d_infectiousAdultTick + Out_f_inf_NyonRes * ( 1 - p_diapause);
								neighbor.state.d_infectiousEgg = neighbor.state.d_infectiousEgg + Out_f_inf_AdonRes * adEggRate * effTicktoEgg * ( 1 - p_diapause);
								
								if(diapause){
									neighbor.state.dp_d_totoAdultTick = neighbor.state.dp_d_totoAdultTick + Out_f_NyonRes * p_diapause;	
									neighbor.state.dp_d_totoEgg = neighbor.state.dp_d_totoEgg + Out_f_AdonRes * adEggRate * p_diapause;
									neighbor.state.dp_d_infectiousAdultTick = neighbor.state.dp_d_infectiousAdultTick + Out_f_inf_NyonRes * p_diapause;
									neighbor.state.dp_d_infectiousEgg = neighbor.state.dp_d_infectiousEgg + Out_f_inf_AdonRes * adEggRate * effTicktoEgg * p_diapause;
								}
								
								break RsDispersive;
								
							}
						}		
					}				
				}				
			}								
		}
		
		
		///////////////////////////////////////////////////////////////////
		////////////////////// livestock movement /////////////////////////
		///////////////////////////////////////////////////////////////////
		
		if(mm2){
			
			//////////////////////////////
			////////Home-ranging//////////
			//////////////////////////////
			
			GridCellNgh <Object> nghCreator = new GridCellNgh<Object>(grid, grid.getLocation (this), Object.class, 1, 1);
			List <GridCell<Object >> gridCells = nghCreator.getNeighborhood(false);
			SimUtilities.shuffle(gridCells, RandomHelper.getUniform());	

			// Get the states of the 8 neighbors
			for (GridCell<Object>cell:gridCells) {
				
				LvHomeRanging: {				
					CAMPCA neighbor = new CAMPCA();	
					
					if (grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY()) == null) break LvHomeRanging;
					else if (grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY()).getClass().equals(CAMPCA.class)) {
						neighbor = (CAMPCA) grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY());
					}
					else break LvHomeRanging; 
					
					double d_z = neighbor.state.elevation - ccc.elevation;					
					Random rr = new Random();
					
					MCLivestock = updateMCLivestock(monthinyear);
					double liv_move = rr.nextGaussian() * MCLivestock;				
					int rrr1 = (int)(Math.sqrt(Math.pow(liv_move,2) - Math.pow(d_z, 2))); //^2 - d_z^2;
					
					if (rrr1 > MCLivestock ){
						
						boolean flag1 = false;	
						
						double rr_1 = Math.random();//if (rr_1>1) rr_1 = 1;
						double Out_totoLivestockHost = rr_1 * ccc.totoLivestockHost/8;	
						
						if ((neighbor.state.totoLivestockHost + Out_totoLivestockHost) < capacityLivGrass) flag1 = true;
						
						//System.out.println("Livestock moving");	
						//boolean isLivHabitat = isLivestockHabitat(neighbor.state.landscape);	
						
						double pinDistination = 0;
						if (flag1) {	
							if(neighbor.state.landscape == 1 || neighbor.state.landscape == 2) pinDistination = 0;
							if(neighbor.state.landscape == 3) pinDistination = 0.5;
							if(neighbor.state.landscape == 4) pinDistination = 0.2;
							if(neighbor.state.landscape == 5 || neighbor.state.landscape == 6) pinDistination = 0.1;					
						}

						if (pinDistination >0) {									
							
							//ccc.totoLivestockHost = ccc.totoLivestockHost - Out_totoLivestockHost;						
							//System.out.println(pinDistination);
							double prop_onLiv = ccc.totoLivestockHost/(ccc.totoLivestockHost+ccc.totoReproductionHost);	
							//double p_liv_out = Math.pow((MCLivestock/500),2);			
							
							double Out_f_NyonLiv = rr_1 * feeding_NyonRepro * pinDistination * prop_onLiv /8;	
							double Out_f_AdonLiv = rr_1 * feeding_AdonRepro * pinDistination * prop_onLiv /8;
							double Out_f_inf_NyonLiv = rr_1 * feeding_inf_NyonRepro * pinDistination * prop_onLiv /8;	
							double Out_f_inf_AdonLiv = rr_1 * feeding_inf_AdonRepro * pinDistination * prop_onLiv /8;
							
							double In_f_NyonLiv = 0;
							double In_f_AdonLiv = 0;
							double In_f_inf_NyonLiv = 0;
							double In_f_inf_AdonLiv = 0;
													
							if (neighbor.state.totoNymphTick > 0){
								In_f_NyonLiv = neighbor.state.totoNymphTick * rate_NyOnRp * pinDistination * f_questing;
								In_f_NyonLiv = In_f_NyonLiv * checkValue(survival_feeding_Ny - 0.049 *Math.log(1.01+In_f_NyonLiv/Out_totoLivestockHost));
								if (In_f_NyonLiv > Out_totoLivestockHost * mmxNyOnRp * pinDistination) {
									In_f_NyonLiv = Out_totoLivestockHost * mmxNyOnRp * pinDistination;
								}
								
								In_f_inf_NyonLiv = In_f_NyonLiv * (neighbor.state.infectiousNymphTick/neighbor.state.totoNymphTick);
								
								neighbor.state.totoNymphTick = neighbor.state.totoNymphTick - In_f_NyonLiv;
								neighbor.state.infectiousNymphTick = neighbor.state.infectiousNymphTick - In_f_inf_NyonLiv;
							} 

							if (neighbor.state.totoAdultTick > 0){
								In_f_AdonLiv = neighbor.state.totoAdultTick * rate_AdOnRp * pinDistination * f_questing * 0.5;
								In_f_AdonLiv = In_f_AdonLiv * checkValue(survival_feeding_Ad - 0.049 *Math.log(1.01+In_f_AdonLiv/Out_totoLivestockHost));
								if (In_f_AdonLiv > Out_totoLivestockHost * mmxAdOnRp * pinDistination ) {
									In_f_AdonLiv = Out_totoLivestockHost * mmxAdOnRp * pinDistination;
								}	
								In_f_inf_AdonLiv = In_f_AdonLiv * (neighbor.state.infectiousAdultTick/neighbor.state.totoAdultTick);
								neighbor.state.totoAdultTick = neighbor.state.totoAdultTick - In_f_AdonLiv;
								neighbor.state.infectiousAdultTick = neighbor.state.infectiousAdultTick - In_f_inf_AdonLiv;
							}
							
							//neighbor.state.totoLivestockHost = neighbor.state.totoLivestockHost + Out_totoLivestockHost;
							
							feeding_NyonRepro = feeding_NyonRepro - Out_f_NyonLiv + In_f_NyonLiv;
							feeding_AdonRepro = feeding_AdonRepro - Out_f_AdonLiv + In_f_AdonLiv;
							feeding_inf_NyonRepro = feeding_inf_NyonRepro - Out_f_inf_NyonLiv + In_f_inf_NyonLiv;
							feeding_inf_AdonRepro = feeding_inf_AdonRepro - Out_f_inf_AdonLiv + In_f_inf_AdonLiv;
							
							neighbor.state.d_totoAdultTick = neighbor.state.d_totoAdultTick + Out_f_NyonLiv * ( 1 - p_diapause);	
							neighbor.state.d_totoEgg = neighbor.state.d_totoEgg + Out_f_AdonLiv * adEggRate * ( 1 - p_diapause);
							neighbor.state.d_infectiousAdultTick = neighbor.state.d_infectiousAdultTick + Out_f_inf_NyonLiv * ( 1 - p_diapause);
							neighbor.state.d_infectiousEgg = neighbor.state.d_infectiousEgg + Out_f_inf_AdonLiv * adEggRate * effTicktoEgg * ( 1 - p_diapause);
							
							if(diapause){
								neighbor.state.dp_d_totoAdultTick = neighbor.state.dp_d_totoAdultTick + Out_f_NyonLiv * p_diapause;	
								neighbor.state.dp_d_totoEgg = neighbor.state.dp_d_totoEgg + Out_f_AdonLiv * adEggRate * p_diapause;
								neighbor.state.dp_d_infectiousAdultTick = neighbor.state.dp_d_infectiousAdultTick + Out_f_inf_NyonLiv * p_diapause;
								neighbor.state.dp_d_infectiousEgg = neighbor.state.dp_d_infectiousEgg + Out_f_inf_AdonLiv * adEggRate * effTicktoEgg * p_diapause;
							}
						}			
					}
				
				}
				
			}					
		}

		///////////////////////////////////////////////////////////////////
		////////////////// reproduction host movement//////////////////////
		///////////////////////////////////////////////////////////////////
		
		if(mm3){				
						
			double prop_onRepro = ccc.totoReproductionHost/(ccc.totoLivestockHost+ccc.totoReproductionHost);
			
			boolean summeruphill = false;
			boolean winterdownhill = false;
			
			propDisRep = 0;
			/*
			if (monthinyear >= 3 && monthinyear <= 6){
				summeruphill = true;
				propDisRep = 0.5/10;		
			}
			
			if (monthinyear >= 8){
				winterdownhill = true;
				propDisRep = 0.5/20;
			}*/
					
			//////////////////////////////
			////////Home-ranging//////////
			//////////////////////////////  
			
			double newMCReproduction = 0;
			
			MCReproduction = updateMCReproduction(monthinyear);
			
			if(ccc.elevation>-100) {
				newMCReproduction = Math.sqrt((int)(ccc.elevation/200 + 0.5) * 1000000 + MCReproduction*MCReproduction*4)/2;
			}
			else newMCReproduction = MCReproduction;
			//System.out.println(newMCReproduction - MCReproduction);
			
			//GridCellNgh <CAMPCA> nghCreator = new GridCellNgh<CAMPCA>(grid, grid.getLocation(this), CAMPCA.class, 1, 1);
			//List <GridCell<CAMPCA >> gridCells = nghCreator.getNeighborhood(false);
			//SimUtilities.shuffle(gridCells, RandomHelper.getUniform());	
			
			GridCellNgh <Object> nghCreator = new GridCellNgh<Object>(grid, grid.getLocation(this), Object.class, 1, 1);
			List <GridCell<Object >> gridCells = nghCreator.getNeighborhood(false);
			SimUtilities.shuffle(gridCells, RandomHelper.getUniform());	
			
			for (GridCell<Object>cell:gridCells) {
				//int rrr2 = (int)(Math.abs(rr.nextGaussian() * newMCReproduction));				
				RpHomeRanging: {
					
					CAMPCA neighbor = new CAMPCA();	
					if (grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY()) == null) break RpHomeRanging;
					else if (grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY()).getClass().equals(CAMPCA.class)) {
						neighbor = (CAMPCA) grid.getObjectAt(cell.getPoint().getX(),cell.getPoint().getY());
					}
					else break RpHomeRanging; 

					Random rr = new Random(); 
					//int rrr2 = (int)(Math.abs((rr.nextGaussian() * newMCReproduction))/1000 + 0.5);
					double rep_move = rr.nextGaussian() * newMCReproduction;
					double d_z = neighbor.state.elevation - ccc.elevation;
					int rrr2 = (int)(Math.sqrt(Math.pow(rep_move,2) - Math.pow(d_z, 2))); 
					
					//if (rr_1>1) rr_1 = 1;					
					double outmovingRep = ccc.totoReproductionHost * (1 - propDisRep)/8;
					//boolean isGrassland = isLivestockHabitat(extendedneighbor.state.landscape);
					
					if (rrr2 > newMCReproduction){	
						double pinDistination = 0;						
						
						if (neighbor.state.landscape == 4 || neighbor.state.landscape == 5 || neighbor.state.landscape == 6 ) {
							pinDistination = propinGland; // grazing 
							//System.out.println(neighbor.state.landscape + ";;;" + pinDistination);
						}
						
						boolean flag1 = false;
						boolean flag2 = false;
						
						double capacityRep = capacityRepWood * neighbor.state.deerSuitability;						
						if ((neighbor.state.totoReproductionHost + outmovingRep) < capacityRep) flag1 = true; 
						if (neighbor.state.landscape == 1 || neighbor.state.landscape == 2) flag2 = true;
						
						if (flag1 && flag2) {
							pinDistination = 0.5; // ranging							
						}
												
						if(pinDistination >0){
							
							double rr_1 = Math.random();
							double Out_totoReproductionHost = rr_1 * outmovingRep; 
							
							double Out_f_NyonRep = rr_1 * feeding_NyonRepro * prop_onRepro * (1 - propDisRep) * pinDistination /8;	
							double Out_f_AdonRep = rr_1 * feeding_AdonRepro * prop_onRepro * (1 - propDisRep) * pinDistination /8;
							double Out_f_inf_NyonRep = rr_1 * feeding_inf_NyonRepro * prop_onRepro * (1 - propDisRep) * pinDistination /8;	
							double Out_f_inf_AdonRep = rr_1 * feeding_inf_AdonRepro * prop_onRepro * (1 - propDisRep) * pinDistination /8;
							
							double In_f_NyonRep = 0;
							double In_f_AdonRep = 0;
							double In_f_inf_NyonRep = 0;
							double In_f_inf_AdonRep = 0;
							
							if (neighbor.state.totoNymphTick > 0){
								In_f_NyonRep = neighbor.state.totoNymphTick * rate_NyOnRp * pinDistination * f_questing * survival_NyonRepro;
								In_f_NyonRep = In_f_NyonRep * checkValue(survival_feeding_Ny - 0.049 *Math.log(1.01+In_f_NyonRep/Out_totoReproductionHost));
								if (In_f_NyonRep > Out_totoReproductionHost * mmxNyOnRp * pinDistination) {
									In_f_NyonRep = Out_totoReproductionHost * mmxNyOnRp * pinDistination;
								}
								
								In_f_inf_NyonRep = In_f_NyonRep * (neighbor.state.infectiousNymphTick/neighbor.state.totoNymphTick);
								
								neighbor.state.totoNymphTick = neighbor.state.totoNymphTick - In_f_NyonRep;
								neighbor.state.infectiousNymphTick = neighbor.state.infectiousNymphTick - In_f_inf_NyonRep;
							} 

							if (neighbor.state.totoAdultTick > 0){
								In_f_AdonRep = neighbor.state.totoAdultTick * rate_AdOnRp * pinDistination * f_questing * survival_AdonRepro * 0.5;
								In_f_AdonRep = In_f_AdonRep * checkValue(survival_feeding_Ad - 0.049 *Math.log(1.01+In_f_AdonRep/Out_totoReproductionHost));
								if (In_f_AdonRep > Out_totoReproductionHost * mmxAdOnRp * pinDistination ) {
									In_f_AdonRep = Out_totoReproductionHost * mmxAdOnRp * pinDistination;
								}	
								In_f_inf_AdonRep = In_f_AdonRep * (neighbor.state.infectiousAdultTick/neighbor.state.totoAdultTick);
								neighbor.state.totoAdultTick = neighbor.state.totoAdultTick - In_f_AdonRep;
								neighbor.state.infectiousAdultTick = neighbor.state.infectiousAdultTick - In_f_inf_AdonRep;
							}
							
							feeding_NyonRepro = feeding_NyonRepro - Out_f_NyonRep + In_f_NyonRep;
							feeding_AdonRepro = feeding_AdonRepro - Out_f_AdonRep + In_f_AdonRep;
							feeding_inf_NyonRepro = feeding_inf_NyonRepro - Out_f_inf_NyonRep + In_f_inf_NyonRep;
							feeding_inf_AdonRepro = feeding_inf_AdonRepro - Out_f_inf_AdonRep + In_f_inf_AdonRep;
			
							neighbor.state.d_totoEgg = neighbor.state.d_totoEgg + Out_f_AdonRep * adEggRate * (1 - p_diapause);	
							neighbor.state.d_totoAdultTick = neighbor.state.d_totoAdultTick + Out_f_NyonRep * (1 - p_diapause);
							neighbor.state.d_infectiousEgg = neighbor.state.d_infectiousEgg + Out_f_inf_AdonRep * adEggRate* effTicktoEgg * (1 - p_diapause);	
							neighbor.state.d_infectiousAdultTick = neighbor.state.d_infectiousAdultTick + Out_f_inf_NyonRep * (1 - p_diapause);
							
							if(diapause){
								neighbor.state.dp_d_totoEgg = neighbor.state.dp_d_totoEgg + Out_f_AdonRep * adEggRate * p_diapause;	
								neighbor.state.dp_d_totoAdultTick = neighbor.state.dp_d_totoAdultTick + Out_f_NyonRep * p_diapause;
								neighbor.state.dp_d_infectiousEgg = neighbor.state.dp_d_infectiousEgg + Out_f_inf_AdonRep * adEggRate* effTicktoEgg * p_diapause;	
								neighbor.state.dp_d_infectiousAdultTick = neighbor.state.dp_d_infectiousAdultTick + Out_f_inf_NyonRep * p_diapause;
							}
						}
											
					}
																		
				}
					
			}			
			
			//////////////////////////////
			//////////////////////////////
			//////////dispersive//////////
			//////////////////////////////
			//////////////////////////////
				
			if (propDisRep >0){
				
				double disRep = ccc.totoReproductionHost * propDisRep;
				double dis = 0;
			
				int thisX = grid.getLocation(this).getX();
				int thisY = grid.getLocation(this).getY();
				int rangM = (int) (MCReproductionDis/1000);
				int i, j;
				
				int targetX = 0;
				int targetY = 0;

				for (int d = 1; d<= rangM; d++){
					j = thisY - d;
					for (i = thisX - d; i<= thisX + d; i++){
						if (grid.getObjectAt(i,j) == null){
							//System.out.println("NOT A HABITAT!!");
						}
						else if (grid.getObjectAt(i,j).getClass().equals(CAMPCA.class)) {			
							
							CAMPCA nnbb = (CAMPCA)grid.getObjectAt(i,j);
							
							boolean flag1 = false;				
							boolean ishabitat = isDeerHabitat(nnbb.state.landscape);
							double capacityRep = capacityRepWood * nnbb.state.deerSuitability;
							//double d_z = grid.getObjectAt(i,j).state.elevation - 
							if (ishabitat && ((nnbb.state.totoReproductionHost + disRep) <capacityRep)) flag1 = true;
							
							boolean flag2 = false;
							if (summeruphill){
								double delta_elevation = nnbb.state.elevation - ccc.elevation;
								if (delta_elevation < 50 && delta_elevation>20 ) flag2 = true;
							}
							if (winterdownhill){
								double delta_elevation = nnbb.state.elevation - ccc.elevation;
								if (delta_elevation < -20 && delta_elevation>-50 ) flag2 = true;
							}
							
							if (flag1 && flag2){
								if (dis == 0){				
									dis = Math.sqrt((thisX-i)*(thisX-i)+(thisY-j)*(thisY-j)); targetX = i; targetY = j; //System.out.println("dis!!" + dis);
								}
								else{
									double disN = Math.sqrt((thisX-i)*(thisX-i)+(thisY-j)*(thisY-j));
									if (disN < dis){
										dis = disN;	targetX = i; targetY = j; //System.out.println("targetX; targetY:" + thisY+";" + thisX);
									}
									else if(disN == dis){
										Random pp = new Random();
										double flag = pp.nextDouble();
										if(flag>0.5) {targetX = i; targetY = j;}								
									}
								}
							}
						}	
					}
					j = thisY + d;
					for (i = thisX - d; i<= thisX + d; i++){
						if (grid.getObjectAt(i,j) == null){
							//System.out.println("NOT A HABITAT!!");
						}
						else if (grid.getObjectAt(i,j).getClass().equals(CAMPCA.class)) {			
							
							CAMPCA nnbb = (CAMPCA)grid.getObjectAt(i,j);
							boolean flag1 = false;
							boolean ishabitat = isDeerHabitat(nnbb.state.landscape);
							double capacityRep = capacityRepWood * nnbb.state.deerSuitability;
							
							if (ishabitat && ((nnbb.state.totoReproductionHost + disRep) <capacityRep)) flag1 = true;
							
							boolean flag2 = false;
							if (summeruphill){
								double delta_elevation = nnbb.state.elevation - ccc.elevation;
								if (delta_elevation < 50 && delta_elevation>20 ) flag2 = true;
							}
							if (winterdownhill){
								double delta_elevation = nnbb.state.elevation - ccc.elevation;
								if (delta_elevation < -20 && delta_elevation>-50 ) flag2 = true;
							}
										
							if (flag1 && flag2){
								if (dis == 0){
									dis = Math.sqrt((thisX-i)*(thisX-i)+(thisY-j)*(thisY-j)); targetX = i; targetY = j;	//System.out.println("dis!!" + dis);
								}
								else{
									double disN = Math.sqrt((thisX-i)*(thisX-i)+(thisY-j)*(thisY-j));
									if (disN < dis){
										dis = disN;	targetX = i; targetY = j;//System.out.println("targetX; targetY:" + thisY+";" + thisX);
									}
									else if(disN == dis){
										Random pp = new Random();
										double flag = pp.nextDouble();
										if(flag>0.5) {targetX = i;targetY = j;}
									}
								}
							}
						}						
					}
					i = thisX - d;
					for (j = thisY - d; j<= thisY + d; j++){
						if (grid.getObjectAt(i,j) == null){
							//System.out.println("NOT A HABITAT!!");
						}
						else if (grid.getObjectAt(i,j).getClass().equals(CAMPCA.class)) {			
							
							CAMPCA nnbb = (CAMPCA)grid.getObjectAt(i,j);
							boolean flag1 = false;
							boolean ishabitat = isDeerHabitat(nnbb.state.landscape);
							double capacityRep = capacityRepWood * nnbb.state.deerSuitability;			
							if (ishabitat && ((nnbb.state.totoReproductionHost + disRep) <capacityRep)) flag1 = true;
							
							boolean flag2 = false;
							if (summeruphill){
								double delta_elevation = nnbb.state.elevation - ccc.elevation;
								if (delta_elevation < 50 && delta_elevation>20 ) flag2 = true;
							}
							if (winterdownhill){
								double delta_elevation = nnbb.state.elevation - ccc.elevation;
								if (delta_elevation < -20 && delta_elevation>-50 ) flag2 = true;
							}
							
							if (flag1 && flag2){
								if (dis == 0){
									dis = Math.sqrt((thisX-i)*(thisX-i)+(thisY-j)*(thisY-j)); targetX = i; targetY = j;	//System.out.println("dis!!" + dis);
								}
								else{
									double disN = Math.sqrt((thisX-i)*(thisX-i)+(thisY-j)*(thisY-j));
									if ( disN < dis){
										dis = disN;	targetX = i; targetY = j;//System.out.println("targetX; targetY:" + thisY+";" + thisX);
									}
									else if(disN == dis){
										Random pp = new Random();
										double flag = pp.nextDouble();
										if(flag>0.5) {targetX = i; targetY = j;}
									}
								}
							}
						}
							
					}
					i = thisX + d;
					for (j = thisY - d; j<= thisY + d; j++){
						if (grid.getObjectAt(i,j) == null){
							//System.out.println("NOT A HABITAT!!");
						}
						else if (grid.getObjectAt(i,j).getClass().equals(CAMPCA.class)) {			
							
							CAMPCA nnbb = (CAMPCA)grid.getObjectAt(i,j); 
							boolean flag1 = false;
							boolean ishabitat = isDeerHabitat(nnbb.state.landscape);
							double capacityRep = capacityRepWood * nnbb.state.deerSuitability;
							if (ishabitat && ((nnbb.state.totoReproductionHost + disRep) <capacityRep)) flag1 = true;
							boolean flag2 = false;
							if (summeruphill){
								double delta_elevation = nnbb.state.elevation - ccc.elevation;
								if (delta_elevation < 50 && delta_elevation>20 ) flag2 = true;
							}
							if (winterdownhill){
								double delta_elevation = nnbb.state.elevation - ccc.elevation;
								if (delta_elevation < -20 && delta_elevation>-50 ) flag2 = true;
							}
							
							if (flag1 && flag2){
								if (dis == 0){
									dis = Math.sqrt((thisX-i)*(thisX-i)+(thisY-j)*(thisY-j)); targetX = i; targetY = j;	//System.out.println("dis!!" + dis);
								}
								else{
									double disN = Math.sqrt((thisX-i)*(thisX-i)+(thisY-j)*(thisY-j));
									if ( disN < dis){
										dis = disN;	targetX = i; targetY = j;//System.out.println("targetX; targetY:" + thisY+";" + thisX);
									}
									else if(disN == dis){
										Random pp = new Random();
										double flag = pp.nextDouble();
										if(flag>0.5) {targetX = i; targetY = j;}
									}
								}
							}
						}
					}
				}
				
				if (dis > 0){
					
					CAMPCA extendedneighbor = new CAMPCA();
					extendedneighbor = (CAMPCA) grid.getObjectAt(targetX,targetY); 		
					
					//double Out_totoReproductionHost = disRep;
					double Out_f_NyonRep = feeding_NyonRepro * prop_onRepro * propDisRep;	
					double Out_f_AdonRep = feeding_AdonRepro * prop_onRepro * propDisRep;
					double Out_f_inf_NyonRep = feeding_inf_NyonRepro * prop_onRepro * propDisRep;	
					double Out_f_inf_AdonRep = feeding_inf_AdonRepro * prop_onRepro * propDisRep;
					
					//ccc.totoReproductionHost = ccc.totoReproductionHost - disRep;

					feeding_NyonRepro = feeding_NyonRepro - Out_f_NyonRep;
					feeding_AdonRepro = feeding_AdonRepro - Out_f_AdonRep;
					feeding_inf_NyonRepro = feeding_inf_NyonRepro - Out_f_inf_NyonRep;
					feeding_inf_AdonRepro = feeding_inf_AdonRepro - Out_f_inf_AdonRep;
					
					//extendedneighbor.state.totoReproductionHost = extendedneighbor.state.totoReproductionHost + disRep;											
					
					extendedneighbor.state.d_totoEgg = extendedneighbor.state.d_totoEgg + Out_f_AdonRep * adEggRate * (1 - p_diapause);	
					extendedneighbor.state.d_totoAdultTick = extendedneighbor.state.d_totoAdultTick + Out_f_NyonRep * (1 - p_diapause);	
					extendedneighbor.state.d_infectiousEgg = extendedneighbor.state.d_infectiousEgg + Out_f_inf_AdonRep * adEggRate * effTicktoEgg * (1 - p_diapause);	
					extendedneighbor.state.d_infectiousAdultTick = extendedneighbor.state.d_infectiousAdultTick + Out_f_inf_NyonRep * (1 - p_diapause);
					
					if(diapause){
						extendedneighbor.state.dp_d_totoEgg = extendedneighbor.state.dp_d_totoEgg + Out_f_AdonRep * adEggRate * p_diapause;	
						extendedneighbor.state.dp_d_totoAdultTick = extendedneighbor.state.dp_d_totoAdultTick + Out_f_NyonRep * p_diapause;	
						extendedneighbor.state.dp_d_infectiousEgg = extendedneighbor.state.dp_d_infectiousEgg + Out_f_inf_AdonRep * adEggRate * effTicktoEgg * p_diapause;	
						extendedneighbor.state.dp_d_infectiousAdultTick = extendedneighbor.state.dp_d_infectiousAdultTick + Out_f_inf_NyonRep * p_diapause;
					}
					//System.out.println(this.state.landscape);
				}
						
			}	
			
		}				
					
		ccc.d_totoEgg = ccc.d_totoEgg + (feeding_AdonReserv + feeding_AdonRepro) * adEggRate * (1 - p_diapause);
		//ccc.d_totoLarvaTick = ccc.d_totoLarvaTick * (1 - p_diapause);
		ccc.d_totoNymphTick = ccc.d_totoNymphTick + (feeding_LaonReserv + feeding_LaonRepro) * (1 - p_diapause);
		ccc.d_totoAdultTick= ccc.d_totoAdultTick + (feeding_NyonReserv + feeding_NyonRepro) * (1 - p_diapause);
		
		ccc.d_infectiousEgg = ccc.d_infectiousEgg + (feeding_inf_AdonReserv + feeding_inf_AdonRepro) * adEggRate * effTicktoEgg * (1 - p_diapause);
		//ccc.d_infectiousLarvaTick = ccc.d_infectiousLarvaTick * (1 - p_diapause);
		ccc.d_infectiousNymphTick = ccc.d_infectiousNymphTick + (feeding_inf_LaonReserv + feeding_inf_LaonRepro) * (1 - p_diapause);
		ccc.d_infectiousAdultTick = ccc.d_infectiousAdultTick + (feeding_inf_NyonReserv + feeding_inf_NyonRepro) * (1 - p_diapause);
		
		if(diapause){
			ccc.dp_d_totoEgg = ccc.dp_d_totoEgg + (feeding_AdonReserv + feeding_AdonRepro) * adEggRate * p_diapause;
			//ccc.dp_d_totoLarvaTick = ccc.dp_d_totoLarvaTick + ccc.d_totoLarvaTick * p_diapause;
			ccc.dp_d_totoNymphTick = ccc.dp_d_totoNymphTick + (feeding_LaonReserv + feeding_LaonRepro) * p_diapause;
			ccc.dp_d_totoAdultTick= ccc.dp_d_totoAdultTick + (feeding_NyonReserv + feeding_NyonRepro) * p_diapause;
			
			ccc.dp_d_infectiousEgg = ccc.dp_d_infectiousEgg + (feeding_inf_AdonReserv + feeding_inf_AdonRepro) * adEggRate * effTicktoEgg * p_diapause;
			//ccc.dp_d_infectiousLarvaTick = ccc.dp_d_infectiousLarvaTick + ccc.d_infectiousLarvaTick * p_diapause;
			ccc.dp_d_infectiousNymphTick = ccc.dp_d_infectiousNymphTick + (feeding_inf_LaonReserv + feeding_inf_LaonRepro) * p_diapause;
			ccc.dp_d_infectiousAdultTick = ccc.dp_d_infectiousAdultTick + (feeding_inf_NyonReserv + feeding_inf_NyonRepro) * p_diapause;
			
			//System.out.println("diapause");
		}
		
		//setState(ccc);	
		
	}
	
	@ScheduledMethod(start=0, interval=1, priority=1)
	public void step2(){
		
		setState(this.state);	
		
	}
	
	public CellStates getState() {
		return state;
	}	
	public CellStates getOldState() {
		return oldState;
	}
	public void setOldState(CellStates oldState) {
		this.oldState = oldState;
	}
	
	public void setState(CellStates newstate) {
		this.state = newstate;	
		// also store the state in the value layer for animation
		Context<Object> context = ContextUtils.getContext(this);
		Grid<Object> grid = (Grid)context.getProjection("Grid");
		
		GridValueLayer v0 = (GridValueLayer)context.getValueLayer("TotoLarvaTick");
		GridValueLayer v1 = (GridValueLayer)context.getValueLayer("TotoNymphTick");
		GridValueLayer v2 = (GridValueLayer)context.getValueLayer("TotoAdultTick");
		GridValueLayer v3 = (GridValueLayer)context.getValueLayer("TotoReserviorHost");
		GridValueLayer v4 = (GridValueLayer)context.getValueLayer("TotoReproductionHost");
		GridValueLayer v5 = (GridValueLayer)context.getValueLayer("TotoLivestockHost");
		GridValueLayer v6 = (GridValueLayer)context.getValueLayer("NymphalInfectionPrevalence");
		GridValueLayer v7 = (GridValueLayer)context.getValueLayer("InfectiousNymphTick");
		GridValueLayer v8 = (GridValueLayer)context.getValueLayer("ContactRate");

		//GridValueLayer v6 = (GridValueLayer)context.getValueLayer("InfectiousLarvaTick");
		//GridValueLayer v7 = (GridValueLayer)context.getValueLayer("InfectiousNymphTick");
		//GridValueLayer v8 = (GridValueLayer)context.getValueLayer("InfectiousAdultTick");
		//GridValueLayer v9 = (GridValueLayer)context.getValueLayer("InfectiousReserviorHost");
		//GridValueLayer v10 = (GridValueLayer)context.getValueLayer("Landscape");
		
		v0.set(newstate.totoLarvaTick, grid.getLocation(this).getX(), grid.getLocation(this).getY());
		v1.set(newstate.totoNymphTick, grid.getLocation(this).getX(), grid.getLocation(this).getY());
		v2.set(newstate.totoAdultTick, grid.getLocation(this).getX(), grid.getLocation(this).getY());
		v3.set(newstate.totoReserviorHost, grid.getLocation(this).getX(), grid.getLocation(this).getY());
		v4.set(newstate.totoReproductionHost, grid.getLocation(this).getX(), grid.getLocation(this).getY());
		v5.set(newstate.totoLivestockHost, grid.getLocation(this).getX(), grid.getLocation(this).getY());
		double nip = 0;
		if (newstate.totoNymphTick>0) nip = newstate.infectiousNymphTick * 100/newstate.totoNymphTick;
		//System.out.println(nip);
		v6.set(nip, grid.getLocation(this).getX(), grid.getLocation(this).getY());
		v7.set(newstate.infectiousNymphTick, grid.getLocation(this).getX(), grid.getLocation(this).getY());
		v8.set(newstate.a_contactRate_tick_habitat, grid.getLocation(this).getX(), grid.getLocation(this).getY());
		//v6.set(newstate.infectiousLarvaTick, grid.getLocation(this).getX(), grid.getLocation(this).getY());
		//v7.set(newstate.infectiousNymphTick, grid.getLocation(this).getX(), grid.getLocation(this).getY());
		//v8.set(newstate.infectiousAdultTick, grid.getLocation(this).getX(), grid.getLocation(this).getY());
		//v9.set(newstate.infectiousReserviorHost, grid.getLocation(this).getX(), grid.getLocation(this).getY());
		//v10.set(newstate.landscape, grid.getLocation(this).getX(), grid.getLocation(this).getY());			
	}
	
	public double getContactRateHabitat(){
		double c_rate = this.state.a_contactRate_tick_habitat;
		if (c_rate <0) c_rate = 0;	
		return c_rate;
	}
	
	public double getContactLgSizeHabitat() {			
		return checkValue(Math.log(this.state.a_contactRate_tick_habitat));	
	}
	
	public double getContactRateResdential(){
		double c_rate = this.state.a_contactRate_residential;
		if (c_rate <0) c_rate = 0;	
		return c_rate;
	}
	
	public double getContactLgSizeResdential() {		
		//if (lg_size <0) lg_size = 0;		
		return checkValue(Math.log(this.state.a_contactRate_residential));	
	}
	
	public double getTotoLarvaTickNo(){
		return this.state.totoLarvaTick;
	}
	
	public double getTotoNymphTickNo(){
		return this.state.totoNymphTick;
	}

	public double getTotoAdultTickNo(){
		return this.state.totoAdultTick;
	}
	
	public double getInfectiousLarvaTickNo(){
		return this.state.infectiousLarvaTick;
	}
	
	public double getInfectiousNymphTickNo(){
		return this.state.infectiousNymphTick;
	}

	public double getInfectiousAdultTickNo(){
		return this.state.infectiousAdultTick;
	}
	
	public double get_d_TotoLarvaTickNo(){
		return this.state.d_totoLarvaTick;
	}
	
	public double get_d_TotoNymphTickNo(){
		return this.state.d_totoNymphTick;
	}

	public double get_d_TotoAdultTickNo(){
		return this.state.d_totoAdultTick;
	}
	
	public double get_d_InfectiousLarvaTickNo(){
		return this.state.d_infectiousLarvaTick;
	}
	
	public double get_d_InfectiousNymphTickNo(){
		return this.state.d_infectiousNymphTick;
	}

	public double get_d_InfectiousAdultTickNo(){
		return this.state.d_infectiousAdultTick;
	}
	
	public double getTotoQuestingLarvaTickNo(){
		return this.state.totoLarvaTick * f_questing;
	}
	
	public double getTotoQuestingNymphTickNo(){
		return this.state.totoNymphTick * f_questing;
	}

	public double getTotoQuestingAdultTickNo(){
		return this.state.totoAdultTick * f_questing;
	}
	
	public double getInfectiousQuestingLarvaTickNo(){
		return this.state.infectiousLarvaTick * f_questing;
	}
	
	public double getInfectiousQuestingNymphTickNo(){
		return this.state.infectiousNymphTick * f_questing;
	}

	public double getInfectiousQuestingAdultTickNo(){
		return this.state.infectiousAdultTick * f_questing;
	}
	
	public double getTotoReserviorHostNo(){
		return this.state.totoReserviorHost;
	}
	
	public double getTotoReserviorHostNo_1_p(){
		return this.state.totoReserviorHost/1000;
	}
	
	public double getTotoReproductionHostNo(){
		//System.out.println("totoReproductionHost = " + this.state.totoReproductionHost);
		return this.state.totoReproductionHost;
	}
	
	public double getInfectiousReserviorHostNo(){
		return this.state.infectiousReserviorHost;
	}
	
	public double getInfectiousReserviorHostNo_1_p(){
		return this.state.infectiousReserviorHost/1000;
	}
	
	public double getTotoLivestockHostNo(){
		//System.out.println("totoReproductionHost = " + this.state.totoReproductionHost);
		return this.state.totoLivestockHost;
	}

	public double getLandscape(){
		return this.state.landscape;
	}
	
	public double getCell_ID(){
		return this.state.cell_ID;
	}
	
	public int region_ID(){
		return this.state.region_ID;
	}
	
	public double getInfecitousNymphsinGrassland(){
		if (this.state.landscape == 4){
			return this.state.infectiousNymphTick * f_questing;
		}
		else return 0;
	}
	
	public double getInfecitousNymphsinWoodland(){
		if (this.state.landscape == 1 || this.state.landscape == 2 || this.state.landscape == 3){
			return this.state.infectiousNymphTick * f_questing;
		}
		else return 0;
	}
	
	public double getInfecitousLarvaeinWoodland(){
		if (this.state.landscape == 1 || this.state.landscape == 2 || this.state.landscape == 3){
			return this.state.infectiousLarvaTick * f_questing;
		}
		else return 0;
	}
	
	public double getInfecitousAdultsinWoodland(){
		if (this.state.landscape == 1 || this.state.landscape == 2 || this.state.landscape == 3){
			return this.state.infectiousAdultTick * f_questing;
		}
		else return 0;
	}
	
	public double getInfecitousNymphsinHeathland(){
		if (this.state.landscape == 5){
			return this.state.infectiousNymphTick * f_questing;
		}
		else return 0;
	}
	
	public double getTotoNymphsinGrassland(){
		if (this.state.landscape == 4){
			return this.state.totoNymphTick * f_questing;
		}
		else return 0;
	}
	
	public double getTotoNymphsinWoodland(){
		if (this.state.landscape == 1 || this.state.landscape == 2 || this.state.landscape == 3){
			return this.state.totoNymphTick * f_questing;
		}
		else return 0;
	}
	
	public double getTotoLarvaesinWoodland(){
		if (this.state.landscape == 1 || this.state.landscape == 2 || this.state.landscape == 3){
			return this.state.totoLarvaTick * f_questing;
		}
		else return 0;
	}
	
	public double getTotoAdultsinWoodland(){
		if (this.state.landscape == 1 || this.state.landscape == 2 || this.state.landscape == 3){
			return this.state.totoAdultTick * f_questing;
		}
		else return 0;
	}
	
	public double getTotoNymphsinHeathland(){
		if (this.state.landscape == 5){
			return this.state.totoNymphTick * f_questing;
		}
		else return 0;
	}
	
	private double checkValue(double value){
		if (value > 1e-8) return value;
		else return 0;
	}
	
	private double update_birthRes(int month){
		double t = (double) month/12;
		//System.out.println(t);
		double n_rate = 7.5 *(Math.abs(Math.sin(2*Math.PI*(t-0.15)))+ Math.sin(2*Math.PI*(t-0.15)));
		//System.out.println(n_rate);
		if (n_rate > 0) return n_rate;
		else return 0;
	}
	
	private double update_capacityRes(int month){
		double t = (double) month/12;
		double n_K = 6*(Math.pow((10+Math.cos(2*Math.PI*(t+0.35)/3)), 2)-8*Math.sin(2*Math.PI*(t+0.35)/3));
		//System.out.println(n_K);
		if (n_K > 0) return n_K;
		else return 0;
	}
	
	private double update_mortalityRes(double capacity){
		double n_rate = (10 - 2.5)/capacity;
		//System.out.println(n_rate);
		if (n_rate  > 0) return n_rate;
		else return 0;
	}
	
	private double update_f_Eggs(double hostDensity, double feedingAdults){
		//int WW = month%52;		
		double f_Eggs = 1 - (0.01+(0.04*Math.log(1.01 + feedingAdults/(100 * hostDensity))));		
		if (f_Eggs > 0) return f_Eggs;
		else return 0;		
	}
	
	private double update_survival_feeding_La(double hostDensity, double feedingTicks){
		//int WW = month%52;
		double feeding_rate = 0.5 + (0.049*Math.log(1.01 + feedingTicks/hostDensity));		
		if (feeding_rate > 0) return feeding_rate;
		else return 0;		
	}
	
	private double update_survival_feeding_Ny(double hostDensity, double feedingTicks){
		//int WW = month%52;		
		double feeding_rate = 0.5 + (0.049*Math.log(1.01 + feedingTicks/hostDensity));	
		if (feeding_rate > 0) return feeding_rate;
		else return 0;		
	}
	
	private double update_survival_feeding_Ad(double hostDensity, double feedingTicks){
		//int WW = month%52;		
		double feeding_rate = 0.65 + (0.049*Math.log(1.01 + feedingTicks/hostDensity));		
		if (feeding_rate > 0) return feeding_rate;
		else return 0;		
	}
	
	private double update_develop_Ad_Eg(double temp){
		// 0.0001, 0.01, -0.062, 8.7
		double new_rate = 0;	
		if (temp > 5) new_rate = 0.0001*temp*temp + 0.01*temp - 0.062;
		new_rate = 1- Math.pow((1-new_rate),30);
		if (new_rate < 0) new_rate = 0;
		else if(new_rate > 1) new_rate = 1;
		return new_rate;
	}
	
	private double update_develop_Eg_La(double temp){
		// -0.00001, 0.002, -0.019, 8.4
		double new_rate = 0;	
		if (temp > 5) new_rate = -0.00001*temp*temp + 0.002*temp - 0.019; 
		new_rate = 1- Math.pow((1-new_rate),30);
		if (new_rate < 0) new_rate = 0;
		else if(new_rate > 1) new_rate = 1;
		return new_rate;
	}
	
	private double update_develop_La_Ny(double temp){
		// 0.00003, 0.00073, -0.007, 7.4
		double new_rate = 0;
		if (temp > 5) new_rate = 0.00003*temp*temp + 0.00073*temp - 0.007; 
		new_rate = 1- Math.pow((1-new_rate),30);
		if (new_rate < 0) new_rate = 0;
		else if(new_rate > 1) new_rate = 1;
		return new_rate;
	}
	
	private double update_develop_Ny_Ad(double temp){
		// -0.000008, 0.0019, -0.016, 8.7
		double new_rate = 0;
		if (temp > 5) new_rate = -0.000008*temp*temp + 0.019*temp - 0.016; 
		new_rate = 1- Math.pow((1-new_rate),30);
		if (new_rate < 0) new_rate = 0;
		else if(new_rate > 1) new_rate = 1;
		return new_rate;
	}
	
	private double update_f_questing(double temp){
		// -0.000008, 0.0019, -0.016, 8.7
		/*double f_q = -0.01056*temp*temp + 0.43158*temp - 3.4237; 
		if (f_q<0) f_q=0; 
		return f_q;*/
		
		double f_q = 0; 
		
		//double dT = 2 *(1 - 2*Math.random());	
		//temp = temp + dT;
		
		if (temp>16.4) temp = 16.4;
		
		f_q = (-0.7917*temp*temp + 25.946*temp-132.13)/100;
		
		if (f_q < 0) f_q = 0;
		if (f_q > 1) f_q = 1;
		
		//if(f_q >0) System.out.println(f_q);
		
		return f_q;
	}
	
	/*
	private double getSfactor(double landType){
		
		double sf;
		
		if (landType == 1) sf = 0.95;
		else if (landType == 2) sf = 1;
		else if (landType == 3) sf = 3;
		else if (landType == 4) sf = 1.25;
		else if (landType == 5) sf = 1.1;
		else if (landType == 6) sf = 2;
		else sf = 1;
		//System.out.println(sf);
		return sf;
		
	}*/
	
	/*
	private double getSuitabilityRes(double landType){
		
		double suitRes;
		
		if (landType == 1) suitRes = 1;
		else if (landType == 2) suitRes = 0.996;
		else if (landType == 3) suitRes = 0.609;
		else if (landType == 4) suitRes = 0.937;
		else if (landType == 5) suitRes = 0.741;
		else if (landType == 6) suitRes = 0.139;
		else suitRes = 0;
		
		//System.out.println(landType + ";;" + suitRes);
		
		return suitRes;
		
	}*/
	
	private boolean isDeerHabitat(double landType){
		
		boolean isHabitat;
		
		if (landType == 1) isHabitat = true;
		else if (landType == 2) isHabitat = true;
		else if (landType == 3) isHabitat = true;
		else if (landType == 5) isHabitat = true;
		else isHabitat = false;
		
		return isHabitat;
		
	}
	
	private boolean isReservoirHabitat(double landType){
		
		boolean isHabitat;
		
		if (landType == 0) isHabitat = false;
		else if (landType == 6) isHabitat = false;
		else isHabitat = true;
		
		return isHabitat;
		
	}
	
	private boolean isLivestockHabitat(double landType){
		
		boolean isHabitat;
		
		if (landType == 3) isHabitat = true;
		else if (landType == 4) isHabitat = true;
		else isHabitat = false;
		
		return isHabitat;
		
	}
	
	private double updateMCLivestock(int monthNo){
		double newMC = 0;
		if (monthNo < 2 || monthNo > 11) newMC= 200;
		else newMC = 400;
		return newMC;
	}
	
	private double updateMCReproduction(int monthNo){
		double newMC = 0;
		if (monthNo < 5 || monthNo > 11) newMC= 500;
		else newMC = 300;
		return newMC;
	}
	
	public double[] refList(double[] oldList){
		
		double[] newList = new double[oldList.length];
		
		for (int i = 0; i < oldList.length - 1; i++){
			newList[i] = oldList[i+1];
			//System.out.println(newList[i]);
		}
		//newList[oldList.length - 1]=0;

		return newList;
	}

	
}	
	
