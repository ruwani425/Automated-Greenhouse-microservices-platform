import express from 'express';
import type { Request, Response } from 'express';
import axios from 'axios';
import { Eureka } from 'eureka-js-client';

const app = express();
app.use(express.json());

const PORT = 8083;

interface TelemetryData {
    deviceId: string;
    zoneId: string;
    temperature: number;
    humidity?: number;
}

interface ZoneThresholds {
    minTemp: number;
    maxTemp: number;
}

interface ActionLog {
    timestamp: string;
    zoneId: string;
    temperature: number;
    action: string;
}

const actionLogs: ActionLog[] = [];

const client = new Eureka({
    instance: {
        app: 'automation-service',
        hostName: 'localhost',
        ipAddr: '127.0.0.1',
        port: { '$': PORT, '@enabled': true },
        vipAddress: 'AUTOMATION-SERVICE',
        dataCenterInfo: { '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo', name: 'MyOwn' },
    },
    eureka: {
        host: 'localhost',
        port: 8761,
        servicePath: '/eureka/apps/',
    },
});
client.start();

app.post('/api/automation/process', async (req: Request, res: Response): Promise<any> => {
    const { zoneId, temperature } = req.body;

    if (!zoneId || temperature === undefined) {
        return res.status(400).json({ error: "Missing zoneId or temperature" });
    }

    try {
        const zoneResponse = await axios.get(`http://localhost:8080/zone-service/api/zones/${zoneId}`);
        const zone = zoneResponse.data;
        // @ts-ignore
        const { minTemperature: minTemp, maxTemperature: maxTemp } = zone;

        let action = "KEEP_STABLE";

        if (temperature > maxTemp) {
            action = "TURN_FAN_ON";
        } else if (temperature < minTemp) {
            action = "TURN_HEATER_ON";
        }

        if (action !== "KEEP_STABLE") {
            const newLog = {
                timestamp: new Date().toISOString(),
                zoneId,
                temperature,
                action
            };
            actionLogs.push(newLog);
            console.log(`[RULE TRIGGERED] Zone: ${zoneId} | Action: ${action}`);
        }

        return res.status(200).json({ status: "Processed", action });

    } catch (error: any) {
        console.error("Failed to fetch thresholds from Zone Service:", error.message);
        return res.status(500).json({ error: "Inter-service communication failed" });
    }
});

app.get('/api/automation/logs', (req: Request, res: Response) => {
    res.status(200).json(actionLogs);
});

app.listen(PORT, () => {
    console.log(`Brain (Automation Service) active on port ${PORT}`);
});