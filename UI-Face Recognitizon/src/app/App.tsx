import { useState } from "react";
import { LayoutDashboard, Users, Clock, User, ScanFace } from "lucide-react";
import { DashboardScreen } from "./components/DashboardScreen";
import { FaceRecognitionScreen } from "./components/FaceRecognitionScreen";
import { PeopleScreen } from "./components/PeopleScreen";
import { HistoryScreen } from "./components/HistoryScreen";
import { ProfileScreen } from "./components/ProfileScreen";
import talangmasLogo from "../imports/Icon-talangmas.jpg";

type Screen = "dashboard" | "scan" | "people" | "history" | "profile";

const navItems: { id: Screen; label: string; Icon: React.ElementType }[] = [
  { id: "dashboard", label: "Beranda", Icon: LayoutDashboard },
  { id: "people", label: "Karyawan", Icon: Users },
  { id: "scan", label: "Scan", Icon: ScanFace },
  { id: "history", label: "Riwayat", Icon: Clock },
  { id: "profile", label: "Profil", Icon: User },
];

export default function App() {
  const [activeScreen, setActiveScreen] = useState<Screen>("dashboard");

  const renderScreen = () => {
    switch (activeScreen) {
      case "dashboard": return <DashboardScreen onScanPress={() => setActiveScreen("scan")} logo={talangmasLogo} />;
      case "scan": return <FaceRecognitionScreen onBack={() => setActiveScreen("dashboard")} logo={talangmasLogo} />;
      case "people": return <PeopleScreen />;
      case "history": return <HistoryScreen />;
      case "profile": return <ProfileScreen logo={talangmasLogo} />;
    }
  };

  return (
    <div className="size-full flex items-center justify-center" style={{ background: "#001a4d" }}>
      {/* Phone frame */}
      <div
        className="relative flex flex-col overflow-hidden shadow-2xl"
        style={{
          width: "min(390px, 100vw)",
          height: "min(844px, 100vh)",
          borderRadius: "clamp(0px, 4vw, 44px)",
          background: "#003399",
          boxShadow: "0 40px 100px rgba(0,0,0,0.7), 0 0 0 1px rgba(255,255,255,0.08)",
        }}
      >
        {/* Status bar */}
        <div className="flex-shrink-0 flex items-center justify-between px-6 pt-3 pb-1" style={{ background: "#003399" }}>
          <span className="text-white" style={{ fontSize: "0.8rem", fontWeight: 700 }}>9:41</span>
          <div className="flex items-center gap-1.5">
            <svg width="16" height="12" viewBox="0 0 16 12" fill="white" opacity={0.9}>
              <rect x="0" y="4" width="3" height="8" rx="0.5"/>
              <rect x="4.5" y="2.5" width="3" height="9.5" rx="0.5"/>
              <rect x="9" y="1" width="3" height="11" rx="0.5"/>
              <rect x="13.5" y="0" width="2.5" height="12" rx="0.5" opacity={0.3}/>
            </svg>
            <svg width="16" height="12" viewBox="0 0 16 12" fill="white" opacity={0.9}>
              <path d="M8 2.5C10.5 2.5 12.7 3.5 14.3 5.1L15.5 3.9C13.6 2 11 1 8 1 5 1 2.4 2 0.5 3.9L1.7 5.1C3.3 3.5 5.5 2.5 8 2.5Z"/>
              <path d="M8 5.5C9.7 5.5 11.3 6.2 12.4 7.4L13.6 6.2C12.2 4.8 10.2 4 8 4 5.8 4 3.8 4.8 2.4 6.2L3.6 7.4C4.7 6.2 6.3 5.5 8 5.5Z"/>
              <circle cx="8" cy="10" r="1.5"/>
            </svg>
            <div className="flex items-center gap-0.5">
              <div className="w-6 h-3 rounded-[3px] border border-white/60 flex items-center p-0.5">
                <div className="h-full bg-white rounded-[2px]" style={{ width: "75%" }} />
              </div>
            </div>
          </div>
        </div>

        {/* Screen content */}
        <div className="flex-1 overflow-hidden">
          {renderScreen()}
        </div>

        {/* Bottom Nav */}
        <div
          className="flex-shrink-0 flex items-center justify-around px-2 pt-2 pb-5 border-t"
          style={{
            background: "rgba(0, 28, 99, 0.97)",
            backdropFilter: "blur(20px)",
            borderColor: "rgba(255,255,255,0.1)",
          }}
        >
          {navItems.map(({ id, label, Icon }) => {
            const isActive = activeScreen === id;
            const isScan = id === "scan";

            if (isScan) {
              return (
                <button
                  key={id}
                  onClick={() => setActiveScreen(id)}
                  className="flex flex-col items-center -mt-5 relative"
                >
                  <div
                    className="w-14 h-14 rounded-2xl flex items-center justify-center shadow-xl transition-transform active:scale-90"
                    style={{
                      background: "linear-gradient(135deg, #FFB800, #FF8C00)",
                      boxShadow: "0 8px 24px rgba(255,184,0,0.45)",
                    }}
                  >
                    <Icon className="w-6 h-6 text-white" />
                  </div>
                  <span
                    className="mt-1.5"
                    style={{ fontSize: "0.6rem", fontWeight: 600, color: isActive ? "#FFB800" : "rgba(255,255,255,0.4)" }}
                  >
                    {label}
                  </span>
                </button>
              );
            }

            return (
              <button
                key={id}
                onClick={() => setActiveScreen(id)}
                className="flex flex-col items-center gap-1 py-1 px-2 rounded-xl transition-all active:scale-90"
              >
                <div className="relative">
                  <Icon
                    className="w-5 h-5 transition-colors"
                    style={{ color: isActive ? "#FFB800" : "rgba(255,255,255,0.4)" }}
                  />
                  {isActive && (
                    <div className="absolute -bottom-1 left-1/2 -translate-x-1/2 w-1 h-1 rounded-full" style={{ background: "#FFB800" }} />
                  )}
                </div>
                <span
                  style={{
                    fontSize: "0.6rem",
                    fontWeight: 600,
                    color: isActive ? "#FFB800" : "rgba(255,255,255,0.4)",
                    lineHeight: 1,
                  }}
                >
                  {label}
                </span>
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
}
