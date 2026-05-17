import { ActivityStateResponse } from "./activity-state.model";
import { ParticipantResponse } from "./participant.model";

export interface ActivityExecutionResponse{
    identificador: string;
    fechaProgramada: string;
    fechaRealizacion: string;
    horaInicio: string;
    horaFin: string;
    estadoActividad: ActivityStateResponse;
}

export interface ActivityExecutionDetailResponse{
    identificador: string;
    fechaProgramada: string;
    fechaRealizacion: string;
    horaInicio: string;
    horaFin: string;
    estadoActividad: ActivityStateResponse;
    participantes: ParticipantResponse[];
}

export interface EditActivityExecutionRquest{
    identificador: string;
    fechaProgramada: string;
}
