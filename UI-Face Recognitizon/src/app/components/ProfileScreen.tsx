import { useState } from "react";
import { LogOut, Shield, Bell, ChevronRight, Moon, Camera, TrendingUp, Award, Zap } from "lucide-react";

const weekData = [
  { day: "Sen", pct: 92 },
  { day: "Sel", pct: 88 },
  { day: "Rab", pct: 95 },
  { day: "Kam", pct: 79 },
  { day: "Jum", pct: 90 },
  { day: "Sab", pct: 0 },
  { day: "Min", pct: 0 },
];

interface Props {
  logo: string;
}

export function ProfileScreen({ logo }: Props) {
  const [darkMode, setDarkMode] = useState(true);
  const [notifEnabled, setNotifEnabled] = useState(true);

  return (
    <div className="flex flex-col h-full overflow-y-auto" style={{ background: "#003399" }}>
      {/* Header */}
      <div
        className="relative px-5 pt-5 pb-8 text-center overflow-hidden"
        style={{ background: "linear-gradient(160deg, #001240 0%, #001f6e 50%, #003399 100%)" }}
      >
        {/* Glow */}
        <div
          className="absolute top-0 left-1/2 -translate-x-1/2 w-64 h-32 opacity-20 rounded-full"
          style={{ background: "#FFB800", filter: "blur(50px)" }}
        />

        {/* Logo brand row */}
        <div className="relative flex items-center justify-center gap-2 mb-6">
          <div className="w-8 h-8 rounded-lg overflow-hidden bg-white flex items-center justify-center" style={{ boxShadow: "0 2px 10px rgba(0,0,0,0.3)" }}>
            <img src={logo} alt="Talangmas" className="w-full h-full object-contain" />
          </div>
          <span className="text-white/60" style={{ fontSize: "0.75rem", fontWeight: 600, letterSpacing: "0.05em" }}>PT TALANGMAS</span>
        </div>

        <div className="relative">
          <div className="relative inline-block mb-3">
            <div
              className="w-20 h-20 rounded-2xl flex items-center justify-center mx-auto shadow-xl"
              style={{
                background: "linear-gradient(135deg, #FFB800, #FF8C00)",
                boxShadow: "0 8px 24px rgba(255,184,0,0.4)",
              }}
            >
              <span className="text-white" style={{ fontSize: "1.5rem", fontWeight: 800 }}>AH</span>
            </div>
            <button
              className="absolute -bottom-1 -right-1 w-7 h-7 rounded-xl flex items-center justify-center"
              style={{ background: "#003399", border: "2px solid #003399" }}
            >
              <div className="w-full h-full rounded-xl flex items-center justify-center" style={{ background: "rgba(255,255,255,0.15)" }}>
                <Camera className="w-3 h-3 text-white" />
              </div>
            </button>
          </div>
          <h1 className="text-white" style={{ fontSize: "1.15rem", fontWeight: 700 }}>Admin HR</h1>
          <p className="text-white/50 mt-0.5" style={{ fontSize: "0.8rem" }}>Superadmin · HR Department</p>
          <div className="flex items-center justify-center gap-1.5 mt-2">
            <div className="w-1.5 h-1.5 bg-green-400 rounded-full" />
            <span style={{ color: "#4ade80", fontSize: "0.75rem", fontWeight: 600 }}>Aktif sekarang</span>
          </div>
        </div>
      </div>

      <div className="px-5 pb-8 space-y-5">
        {/* Stats */}
        <div className="grid grid-cols-3 gap-3 -mt-4 relative z-10">
          {[
            { label: "Kehadiran", value: "97%", icon: TrendingUp, color: "#4ade80" },
            { label: "Streak", value: "18hr", icon: Zap, color: "#FFB800" },
            { label: "Skor", value: "A+", icon: Award, color: "#60a5fa" },
          ].map((s) => (
            <div
              key={s.label}
              className="rounded-2xl p-3.5 text-center shadow-lg"
              style={{ background: "rgba(255,255,255,0.1)", border: "1px solid rgba(255,255,255,0.15)" }}
            >
              <div
                className="w-8 h-8 rounded-xl flex items-center justify-center mx-auto mb-2"
                style={{ background: `${s.color}22` }}
              >
                <s.icon className="w-4 h-4" style={{ color: s.color }} />
              </div>
              <div className="text-white" style={{ fontSize: "1.1rem", fontWeight: 800 }}>{s.value}</div>
              <div className="text-white/50" style={{ fontSize: "0.65rem", fontWeight: 600 }}>{s.label}</div>
            </div>
          ))}
        </div>

        {/* Weekly chart */}
        <div className="rounded-2xl p-4" style={{ background: "rgba(255,255,255,0.07)", border: "1px solid rgba(255,255,255,0.1)" }}>
          <h3 className="text-white mb-4" style={{ fontSize: "0.875rem", fontWeight: 700 }}>Kehadiran Minggu Ini</h3>
          <div className="flex items-end justify-between gap-1.5" style={{ height: "80px" }}>
            {weekData.map((d) => (
              <div key={d.day} className="flex-1 flex flex-col items-center gap-1.5">
                <div className="w-full flex flex-col items-center justify-end" style={{ height: "60px" }}>
                  {d.pct > 0 ? (
                    <div
                      className="w-full rounded-lg"
                      style={{
                        height: `${d.pct * 0.6}%`,
                        background: d.pct >= 90 ? "linear-gradient(to top, #16a34a, #4ade80)" : d.pct >= 75 ? "linear-gradient(to top, #d97706, #FFB800)" : "rgba(255,255,255,0.15)",
                      }}
                    />
                  ) : (
                    <div className="w-full rounded-lg h-1" style={{ background: "rgba(255,255,255,0.08)" }} />
                  )}
                </div>
                <span className="text-white/40" style={{ fontSize: "0.65rem", fontWeight: 600 }}>{d.day}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Settings */}
        <div className="rounded-2xl overflow-hidden" style={{ background: "rgba(255,255,255,0.07)", border: "1px solid rgba(255,255,255,0.1)" }}>
          <div className="px-4 py-3" style={{ borderBottom: "1px solid rgba(255,255,255,0.08)" }}>
            <span className="text-white/35 uppercase tracking-wider" style={{ fontSize: "0.7rem", fontWeight: 700 }}>Preferensi</span>
          </div>
          {[
            {
              icon: Moon, label: "Mode Gelap", sub: "Tampilan saat ini",
              right: (
                <button
                  onClick={() => setDarkMode(!darkMode)}
                  className="w-11 h-6 rounded-full relative transition-colors"
                  style={{ background: darkMode ? "linear-gradient(135deg, #FFB800, #FF8C00)" : "rgba(255,255,255,0.2)" }}
                >
                  <div className={`absolute top-0.5 w-5 h-5 rounded-full bg-white shadow transition-all ${darkMode ? "left-[22px]" : "left-0.5"}`} />
                </button>
              ),
            },
            {
              icon: Bell, label: "Notifikasi", sub: "Push notification",
              right: (
                <button
                  onClick={() => setNotifEnabled(!notifEnabled)}
                  className="w-11 h-6 rounded-full relative transition-colors"
                  style={{ background: notifEnabled ? "linear-gradient(135deg, #FFB800, #FF8C00)" : "rgba(255,255,255,0.2)" }}
                >
                  <div className={`absolute top-0.5 w-5 h-5 rounded-full bg-white shadow transition-all ${notifEnabled ? "left-[22px]" : "left-0.5"}`} />
                </button>
              ),
            },
          ].map((item) => (
            <div
              key={item.label}
              className="flex items-center gap-3 px-4 py-3.5"
              style={{ borderBottom: "1px solid rgba(255,255,255,0.06)" }}
            >
              <div className="w-8 h-8 rounded-xl flex items-center justify-center" style={{ background: "rgba(255,255,255,0.1)" }}>
                <item.icon className="w-4 h-4 text-white/50" />
              </div>
              <div className="flex-1">
                <div className="text-white" style={{ fontSize: "0.875rem", fontWeight: 500 }}>{item.label}</div>
                <div className="text-white/35" style={{ fontSize: "0.75rem" }}>{item.sub}</div>
              </div>
              {item.right}
            </div>
          ))}
        </div>

        {/* Account */}
        <div className="rounded-2xl overflow-hidden" style={{ background: "rgba(255,255,255,0.07)", border: "1px solid rgba(255,255,255,0.1)" }}>
          <div className="px-4 py-3" style={{ borderBottom: "1px solid rgba(255,255,255,0.08)" }}>
            <span className="text-white/35 uppercase tracking-wider" style={{ fontSize: "0.7rem", fontWeight: 700 }}>Akun</span>
          </div>
          <button className="w-full flex items-center gap-3 px-4 py-3.5 active:opacity-70 transition-opacity">
            <div className="w-8 h-8 rounded-xl flex items-center justify-center" style={{ background: "rgba(255,255,255,0.1)" }}>
              <Shield className="w-4 h-4 text-white/50" />
            </div>
            <div className="flex-1 text-left">
              <div className="text-white" style={{ fontSize: "0.875rem", fontWeight: 500 }}>Keamanan & Privasi</div>
              <div className="text-white/35" style={{ fontSize: "0.75rem" }}>Ubah password, 2FA</div>
            </div>
            <ChevronRight className="w-4 h-4 text-white/25" />
          </button>
        </div>

        {/* Logout */}
        <button
          className="w-full flex items-center justify-center gap-2 py-3.5 rounded-2xl transition-opacity active:opacity-70"
          style={{ background: "rgba(248,113,113,0.12)", border: "1px solid rgba(248,113,113,0.25)", color: "#f87171" }}
        >
          <LogOut className="w-4 h-4" />
          <span style={{ fontSize: "0.9rem", fontWeight: 600 }}>Keluar</span>
        </button>

        <p className="text-center text-white/20" style={{ fontSize: "0.7rem" }}>AbsensiApp v2.4.1 · PT Talangmas</p>
      </div>
    </div>
  );
}
