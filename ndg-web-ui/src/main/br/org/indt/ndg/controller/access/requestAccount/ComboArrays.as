/*
*  Copyright (C) 2010  INdT - Instituto Nokia de Tecnologia
*
*  NDG is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public
*  License as published by the Free Software Foundation; either 
*  version 2.1 of the License, or (at your option) any later version.
*
*  NDG is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public 
*  License along with NDG.  If not, see <http://www.gnu.org/licenses/ 
*/

// ActionScript file

	import mx.collections.*;
	import mx.controls.Alert;
	
	private var arrayCountries:Array;
	//private var arrayCompanyType:Array;
	private var arrayIndustry:Array;
	private var arrayCompanySize:Array;
	
	[Bindable] public var countriesAC:ArrayCollection;
	//[Bindable] public var companyTypeAC:ArrayCollection;
	[Bindable] public var industryAC:ArrayCollection;
	[Bindable] public var industrySizeAC:ArrayCollection;
	
	private var cbCountrySelectedIndex:int;
	//private var cbCompanyTypeSelectedIndex:int;
	private var cbIndustrySelectedIndex:int;
	private var cbIndustrySizeSelectedIndex:int;

	private function updateCombos():void{
		arrayCountries =
			[{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboPleaseSelect"), data:""},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboAfghanistan"), data:"AF"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboAlbania"), data:"AL"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboAlgeria"), data:"DZ"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboAndorra"), data:"AD"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboAnguilla"), data:"AI"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboAntigua&Barbuda"), data:"AG"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboArgentina"), data:"AR"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboArmenia"), data:"AA"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboAruba"), data:"AW"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboAustralia"), data:"AU"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboAustria"), data:"AT"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboAzerbaijan"), data:"AZ"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBahamas"), data:"BS"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBahrain"), data:"BH"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBangladesh"), data:"BD"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBarbados"), data:"BB"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBelarus"), data:"BY"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBelgium"), data:"BE"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBelize"), data:"BZ"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBenin"), data:"BJ"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBermuda"), data:"BM"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBhutan"), data:"BT"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBolivia"), data:"BO"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBonaire"), data:"BL"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBosnia&Herzegovina"), data:"BA"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBotswana"), data:"BW"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBrazil"), data:"BR"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBritishIndianOceanTer"), data:"BC"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBrunei"), data:"BN"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBulgaria"), data:"BG"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBurkinaFaso"), data:"BF"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBurundi"), data:"BI"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCambodia"), data:"KH"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCameroon"), data:"CM"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCanada"), data:"CA"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCanaryIslands"), data:"IC"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCapeVerde"), data:"CV"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCaymanIslands"), data:"KY"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCentralAfricanRepublic"), data:"CF"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboChad"), data:"TD"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboChannelIslands"), data:"CD"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboChile"), data:"CL"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboChina"), data:"CN"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboChristmasIsland"), data:"CI"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCocosIsland"), data:"CS"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboColombia"), data:"CO"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboComoros"), data:"CC"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCongo"), data:"CG"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCookIslands"), data:"CK"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCostaRica"), data:"CR"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCoteD'Ivoire"), data:"CT"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCroatia"), data:"HR"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCuba"), data:"CU"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCuracao"), data:"CB"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCyprus"), data:"CY"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCzechRepublic"), data:"CZ"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboDenmark"), data:"DK"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboDjibouti"), data:"DJ"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboDominica"), data:"DM"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboDominicanRepublic"), data:"DO"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboEastTimor"), data:"TM"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboEcuador"), data:"EC"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboEgypt"), data:"EG"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboElSalvador"), data:"SV"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboEquatorialGuinea"), data:"GQ"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboEritrea"), data:"ER"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboEstonia"), data:"EE"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboEthiopia"), data:"ET"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboFalklandIslands"), data:"FA"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboFaroeIslands"), data:"FO"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboFiji"), data:"FJ"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboFinland"), data:"FI"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboFrance"), data:"FR"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboFrenchGuiana"), data:"GF"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboFrenchPolynesia"), data:"PF"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboFrenchSouthernTer"), data:"FS"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboGabon"), data:"GA"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboGambia"), data:"GM"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboGeorgia"), data:"GE"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboGermany"), data:"DE"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboGhana"), data:"GH"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboGibraltar"), data:"GI"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboGreece"), data:"GR"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboGreenland"), data:"GL"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboGrenada"), data:"GD"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboGuadeloupe"), data:"GP"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboGuam"), data:"GU"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboGuatemala"), data:"GT"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboGuinea"), data:"GN"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboGuyana"), data:"GY"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboHaiti"), data:"HT"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboHawaii"), data:"HW"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboHonduras"), data:"HN"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboHongKong"), data:"HK"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboHungary"), data:"HU"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboIceland"), data:"IS"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboIndia"), data:"IN"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboIndonesia"), data:"ID"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboIran"), data:"IA"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboIraq"), data:"IQ"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboIreland"), data:"IR"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboIsleofMan"), data:"IM"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboIsrael"), data:"IL"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboItaly"), data:"IT"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboJamaica"), data:"JM"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboJapan"), data:"JP"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboJordan"), data:"JO"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboKazakhstan"), data:"KZ"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboKenya"), data:"KE"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboKiribati"), data:"KI"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboKoreaNorth"), data:"NK"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboKoreaSouth"), data:"KS"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboKuwait"), data:"KW"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboKyrgyzstan"), data:"KG"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboLaos"), data:"LA"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboLatvia"), data:"LV"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboLebanon"), data:"LB"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboLesotho"), data:"LS"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboLiberia"), data:"LR"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboLibya"), data:"LY"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboLiechtenstein"), data:"LI"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboLithuania"), data:"LT"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboLuxembourg"), data:"LU"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMacau"), data:"MO"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMacedonia"), data:"MK"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMadagascar"), data:"MG"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMalaysia"), data:"MY"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMalawi"), data:"MW"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMaldives"), data:"MV"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMali"), data:"ML"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMalta"), data:"MT"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMarshallIslands"), data:"MH"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMartinique"), data:"MQ"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMauritania"), data:"MR"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMauritius"), data:"MU"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMexico"), data:"MX"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMidwayIslands"), data:"MI"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMoldova"), data:"MD"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMonaco"), data:"MC"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMongolia"), data:"MN"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMontserrat"), data:"MS"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMorocco"), data:"MA"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMozambique"), data:"MZ"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboMyanmar"), data:"MM"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboNambia"), data:"NA"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboNauru"), data:"NU"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboNepal"), data:"NP"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboNetherlandAntilles"), data:"AN"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboNetherlands(Holland,Europe)"), data:"NL"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboNevis"), data:"NV"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboNewCaledonia"), data:"NC"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboNewZealand"), data:"NZ"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboNicaragua"), data:"NI"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboNiger"), data:"NE"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboNigeria"), data:"NG"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboNiue"), data:"NW"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboNorfolkIsland"), data:"NF"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboNorway"), data:"NO"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboOman"), data:"OM"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboPakistan"), data:"PK"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboPalauIsland"), data:"PW"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboPalestine"), data:"PS"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboPanama"), data:"PA"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboPapuaNewGuinea"), data:"PG"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboParaguay"), data:"PY"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboPeru"), data:"PE"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboPhilippines"), data:"PH"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboPitcairnIsland"), data:"PO"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboPoland"), data:"PL"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboPortugal"), data:"PT"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboPuertoRico"), data:"PR"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboQatar"), data:"QA"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboRepublicofMontenegro"), data:"ME"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboRepublicofSerbia"), data:"RS"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboReunion"), data:"RE"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboRomania"), data:"RO"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboRussia"), data:"RU"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboRwanda"), data:"RW"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboStBarthelemy"), data:"NT"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboStEustatius"), data:"EU"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboStHelena"), data:"HE"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboStKitts-Nevis"), data:"KN"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboStLucia"), data:"LC"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboStMaarten"), data:"MB"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboStPierre&Miquelon"), data:"PM"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboStVincent&Grenadines"), data:"VC"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSaipan"), data:"SP"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSamoa"), data:"SO"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSamoaAmerican"), data:"AS"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSanMarino"), data:"SM"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSaoTome&Principe"), data:"ST"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSaudiArabia"), data:"SA"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSenegal"), data:"SN"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSeychelles"), data:"SC"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSierraLeone"), data:"SL"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSingapore"), data:"SG"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSlovakia"), data:"SK"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSlovenia"), data:"SI"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSolomonIslands"), data:"SB"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSomalia"), data:"OI"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSouthAfrica"), data:"ZA"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSpain"), data:"ES"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSriLanka"), data:"LK"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSudan"), data:"SD"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSuriname"), data:"SR"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSwaziland"), data:"SZ"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSweden"), data:"SE"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSwitzerland"), data:"CH"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSyria"), data:"SY"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboTahiti"), data:"TA"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboTaiwan"), data:"TW"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboTajikistan"), data:"TJ"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboTanzania"), data:"TZ"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboThailand"), data:"TH"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboTogo"), data:"TG"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboTokelau"), data:"TK"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboTonga"), data:"TO"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboTrinidad&Tobago"), data:"TT"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboTunisia"), data:"TN"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboTurkey"), data:"TR"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboTurkmenistan"), data:"TU"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboTurks&CaicosIs"), data:"TC"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboTuvalu"), data:"TV"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboUganda"), data:"UG"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboUkraine"), data:"UA"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboUnitedArabEmirates"), data:"AE"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboUnitedKingdom"), data:"GB"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboUnitedStatesofAmerica"), data:"US"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboUruguay"), data:"UY"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboUzbekistan"), data:"UZ"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboVanuatu"), data:"VU"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboVaticanCityState"), data:"VS"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboVenezuela"), data:"VE"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboVietnam"), data:"VN"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboVirginIslands(Brit)"), data:"VB"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboVirginIslands(USA)"), data:"VA"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboWakeIsland"), data:"WK"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboWallis&FutanaIs"), data:"WF"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboYemen"), data:"YE"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboZaire"), data:"ZR"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboZambia"), data:"ZM"},
			{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboZimbabwe"), data:"ZW"}];

		/*
		arrayCompanyType =
			[{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboPleaseSelect"), data:""},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCorporation"), data:"Corporation"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboGovernmentNonUs"), data:"Govt - Non US"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboGovernmentUs"), data:"Govt - US Federal"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboIndividual"), data:"Individual"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboInstructor"), data:"Educational - I"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboProfessional"), data:"Association - Professional"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSchoolLab"), data:"Educational - L"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboStudent"), data:"Educational - S"}];
		*/
		
		arrayIndustry =
			[{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboPleaseSelect"), data:""},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboAccounting"), data:"Accounting"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboAdminstration"), data:"Adminstration"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboAdvert"), data:"Advert./Media/Entertain."},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboBanking"), data:"Banking & Fin. Services"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCallCenter"), data:"Call Center/Cust. Service"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboCommunity"), data:"Community & Sport"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboConstruction"), data:"Construction"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboConsulting"), data:"Consulting & Corp. Strategy"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboEducation"), data:"Education & Training"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboEngineering"), data:"Engineering"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboGovernment"), data:"Government/Defence"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboHealthcare"), data:"Healthcare & Medical"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboHospitality"), data:"Hospitality & Tourism"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboRecruitment"), data:"HR & Recruitment"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboITT"), data:"I.T. & T."},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboInsurance"), data:"Insurance & Superannuation"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboLegal"), data:"Legal"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboManufacturing"), data:"Manufacturing/Operations"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboOilGas"), data:"Mining. Oil & Gas"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboPrimaryIndustry"), data:"Primary Industry"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboRealState"), data:"Real State & Property"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboRetail"), data:"Retail & Consumer Prods."},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSales"), data:"Sales & Marketing"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboScience"), data:"Science & Technology"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSelfEmployment"), data:"Self-Employment"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboTrades"), data:"Trades & Services"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboTransport"), data:"Trasnport & Logistics"}];
			 		 
			 	
		arrayCompanySize =
			[{label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboPleaseSelect"), data:""},
			 {label:"1", data:"1"},
			 {label:"2 - 5", data:"2 - 5"},
			 {label:"6 - 10", data:"6 - 10"},
			 {label:"11 - 50", data:"11 - 50"},
			 {label:"51 - 100", data:"51 - 100"},
			 {label:"101 - 500", data:"101 - 500"},
			 {label:ConfigI18n.getInstance().getStringFile("requestAccount", "comboSizeMoreThan500"), data:"500+"}];

		
		cbCountrySelectedIndex = cbCountry.selectedIndex;
		//cbCompanyTypeSelectedIndex = cbCompanyType.selectedIndex;
		cbIndustrySelectedIndex = cbIndustry.selectedIndex;
		cbIndustrySizeSelectedIndex = cbIndustrySize.selectedIndex;
		
		countriesAC = new ArrayCollection(arrayCountries);
		//companyTypeAC =  new ArrayCollection(arrayCompanyType);
		industryAC =  new ArrayCollection(arrayIndustry);
		industrySizeAC =  new ArrayCollection(arrayCompanySize);
		
		cbCountry.selectedIndex = cbCountrySelectedIndex;
		//cbCompanyType.selectedIndex = cbCompanyTypeSelectedIndex;
		cbIndustry.selectedIndex = cbIndustrySelectedIndex;
		cbIndustrySize.selectedIndex = cbIndustrySizeSelectedIndex;
	}

	
	