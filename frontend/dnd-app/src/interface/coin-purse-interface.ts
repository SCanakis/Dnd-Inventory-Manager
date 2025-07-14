export interface CoinPurse {
    charUuid : string;
    platinum : number;
    gold : number;
    electrum : number;
    silver : number;
    copper : number;
}


export class CoinPurseDTO {
    platinum : number = -1;
    gold : number = -1;
    electrum : number = -1;
    silver : number = -1;
    copper : number = -1;


    constructor() {}

    setPlatinum(platinum : number) : void {
        this.platinum = platinum;
    }

    getPlatinum() : number  {
        return this.platinum;
    }

    setGold(gold : number) : void {
        this.gold = gold;
    }

    getGold() : number {
        return this.gold;
    }

    setElectrum(electrum : number) : void {
        this.electrum = electrum;
    }

    getElectrum() : number {
        return this.electrum;
    }

    setSilver(silver: number) : void {
        this.silver = silver;
    }

    getSilver() : number {
        return this.silver;
    }
 
    setCopper(copper: number) : void {
        this.copper = copper;
    }

    getCopper() : number {
        return this.copper;
    }
 

}