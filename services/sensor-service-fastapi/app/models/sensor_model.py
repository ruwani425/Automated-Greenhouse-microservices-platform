from pydantic import BaseModel
from typing import Optional, Dict

class SensorData(BaseModel):
    deviceId: str
    zoneId: str
    value: Dict[str, float]
    capturedAt: Optional[str] = None