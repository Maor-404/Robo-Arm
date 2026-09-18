from PIL import Image,ImageDraw
from pathlib import Path
root=Path('src/main/resources/assets/roboticarm')
def tex(path,base,accent=None):
 im=Image.new('RGBA',(16,16),base); d=ImageDraw.Draw(im)
 for x in range(0,16,4): d.line((x,0,x,15),fill=tuple(max(0,c-20) for c in base[:3])+(255,))
 if accent: d.rectangle((4,4,11,11),fill=accent)
 im.save(root/path)
tex('textures/block/abandoned_circuit.png',(39,52,57,255),(38,176,163,255)); tex('textures/block/cable_block.png',(29,35,40,255),(35,107,120,255)); tex('textures/block/broken_machine.png',(75,79,77,255),(130,133,121,255)); tex('textures/block/robotic_arm_base.png',(82,91,95,255),(41,184,167,255))
tex('textures/item/power_core.png',(25,35,40,255),(46,210,173,255)); tex('textures/item/processing_core.png',(36,38,49,255),(184,73,230,255)); tex('textures/item/robotic_arm_blueprint.png',(211,194,146,255),(44,106,112,255))
(root/'textures/entity').mkdir(parents=True,exist_ok=True); Image.new('RGBA',(64,64),(71,148,151,255)).save(root/'textures/entity/mysterious_villager.png')
(root/'textures/gui').mkdir(parents=True,exist_ok=True); Image.new('RGBA',(176,222),(28,38,43,255)).save(root/'textures/gui/robotic_arm.png')
