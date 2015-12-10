package main
import (
	"syscall"
	"unsafe"
	common "github.com/shirou/gopsutil/common"
	"time"
	"log"
	"gopkg.in/mgo.v2"	
)
var (
	procGlobalMemoryStatusEx = common.Modkernel32.NewProc("GlobalMemoryStatusEx")
)
type MEMORYSTATUSEX struct {
	cbSize                  uint32
	dwMemoryLoad            uint32
	ullTotalPhys            uint64 // in bytes
	ullAvailPhys            uint64
	ullTotalPageFile        uint64
	ullAvailPageFile        uint64
	ullTotalVirtual         uint64
	ullAvailVirtual         uint64
	ullAvailExtendedVirtual uint64
}

func main(){
	doEvery(30000*time.Millisecond)
}

func doEvery(d time.Duration) {
	for  range time.Tick(d) {
		VirtualMemory()
	}
}

type VirtualMemoryStat struct {
	Total       uint64  `json:"total"`
	Available   uint64  `json:"available"`
	Used        uint64  `json:"used"`
	UsedPercent float64 `json:"used_percent"`
	Free        uint64  `json:"free"`
	Active      uint64  `json:"active"`
	Inactive    uint64  `json:"inactive"`
	Buffers     uint64  `json:"buffers"`
	Cached      uint64  `json:"cached"`
	Wired       uint64  `json:"wired"`
	Shared      uint64  `json:"shared"`
}

type Ctr struct {
  Mem   uint64
  Time 	string
  Instance string
}

func VirtualMemory() (*VirtualMemoryStat, error){
	var memInfo MEMORYSTATUSEX
	memInfo.cbSize = uint32(unsafe.Sizeof(memInfo))
	mem, _, _ := procGlobalMemoryStatusEx.Call(uintptr(unsafe.Pointer(&memInfo)))
	if mem == 0 {
		return nil, syscall.GetLastError()
	}
	ret := &VirtualMemoryStat{
		Total:       memInfo.ullTotalPhys,
		Available:   memInfo.ullAvailPhys,
		UsedPercent: float64(memInfo.dwMemoryLoad),
	}
	ret.Used = ret.Total - ret.Available
	session, err := mgo.Dial("mongodb://bhavana:bhavana@ds037244.mongolab.com:37244/tests")
	c := session.DB("tests").C("memoryctr")
	tim := time.Now().UTC()
    err = c.Insert(&Ctr{Mem : ret.Used,Time : tim.String(),Instance : "i-094c05bb"})
	if err != nil {
		log.Fatal(err)
	}
	return ret, nil
}