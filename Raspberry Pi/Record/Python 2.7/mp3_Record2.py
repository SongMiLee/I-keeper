# A simple recorder based on the voice recorder tutorial code example
 
# by Petri Savolainen (petri.savolainen@iki.fi)
 

 
import time, sys
 

 
import pymedia.audio.sound as sound
 
import pymedia.audio.acodec as acodec
 

 
# constants
 
STOPPED   = 0
 
RECORDING = 1
 

 
# input params
 
insrc = "VIA AC'97 Audio (WAVE)"
 
format = sound.AFMT_U16_LE
 

 
# encoder parameters
 
params = {
 
         'id': acodec.getCodecId("mp3"),
 
         'bitrate': 64000,
 
         'sample_rate': 22050,
 
         'channels': 1
 
}
 

 

 
class Recorder:
 
   
 
   def __init__(self, params=params):
 
      self.status = STOPPED
 
      self.encoder= acodec.Encoder(params)
 

 
      # determine input source
 
      inputid=None
 
      for d in sound.getIDevices():
 
         if d['name'] == insrc:
 
            inputid = d['id']
 
      
 
      if not inputid:
 
         raise Exception("Invalid or no input source")
 

 
      self.snd= sound.Input(22050, 1, format, inputid)
 
   
 
   def bytesWritten(self):
 
      return self.bytecount
 
      
 
   def getDeviceNames(self):
 
      names = []
 
      for d in sound.getIDevices():
 
         names.append(d['name'])
 
      return names
 
      
 
   def setDevice(self, name):
 
      for d in sound.getIDevices():
 
         if d['name'] == name:
 
            self.deviceid = d['id']
 
            return
 
      raise Exception("Invalid device!")
 
      
 
      
 
   def process(self):
 
      #while 1: # snd.getPosition()<= secs:
 
      s= self.snd.getData()
 
      if s and len(s):
 
         for fr in self.encoder.encode(s):
 
           # We definitely should use mux first, but for
 
           # simplicity reasons this way it'll work also
 
           self.bytecount += len(fr)
 
           self.outfile.write(fr)
 
           
 
      #else:
 
      #   time.sleep( .001 )
 
      
 
   def start(self, file):
 
      self.bytecount = 0
 
      self.status = RECORDING
 
      self.outfile= file
 
      self.snd.start()      
 
      
 
   def stop(self):
 
      # Stop listening the incoming sound from the microphone or line in
 
      self.status=STOPPED
 
      self.snd.stop()
 
      self.outfile.close()
 

 
if __name__=="__main__":
 
   r = Recorder()
 



