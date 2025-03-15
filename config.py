# config.py
import os

class Config:
	# 你的文心/DeepSeek接口根路径
	WENXIN_API_URL = "https://qianfan.baidubce.com/v2/chat/completions"
	
	# 这里示例写死，但更安全的做法是用环境变量：
	# WENXIN_API_KEY = os.getenv("WENXIN_API_KEY", "")
	WENXIN_API_KEY = "bce-v3/ALTAK-EV929lNomcyUDXusAKPiy/3671557eef6ae3529b434e7752edef258c68b202"  
	
	REDIS_URL = "redis://localhost:6379/0"

	REDIS_EXPIRE_SECONDS = 1800  # 默认过期时间
