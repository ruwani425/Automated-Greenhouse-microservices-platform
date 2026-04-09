import httpx
import asyncio
from models.sensor_model import SensorData
from datetime import datetime

sensor_history = []
access_token = None


def save_sensor_data(data: SensorData):
    """Saves manually posted sensor data to history."""
    # Convert Pydantic model to a dict and add a timestamp
    record = data.model_dump() 
    record["timestamp"] = datetime.now().isoformat()
    sensor_history.append(record)
    return record

def get_all_sensor_data():
    """Returns all stored telemetry records."""
    return sensor_history

sensor_history = []
access_token = None

async def fetch_external_telemetry():
    global access_token
    if not access_token:
        async with httpx.AsyncClient() as client:
            resp = await client.post("http://104.211.95.241:8080/api/auth/login",
                                     json={"username": "username", "password": "123456"})
            access_token = resp.json().get("accessToken")

    async with httpx.AsyncClient() as client:
        zones_resp = await client.get("http://localhost:8080/zone-service/api/zones")
        if zones_resp.status_code == 200:
            zones = zones_resp.json()
            for zone in zones:
                device_id = zone.get("deviceId")
                if device_id:
                    headers = {"Authorization": f"Bearer {access_token}"}
                    tel_resp = await client.get(f"http://104.211.95.241:8080/api/devices/telemetry/{device_id}", headers=headers)
                    if tel_resp.status_code == 200:
                        data = tel_resp.json()
                        automation_payload = {
                            "zoneId": zone.get("id"),
                            "deviceId": device_id,
                            "temperature": data["value"]["temperature"],
                            "humidity": data["value"]["humidity"],
                            "capturedAt": data["capturedAt"]
                        }
                        await client.post("http://localhost:8080/automation-service/api/automation/process", json=automation_payload)
