import SwiftUI
import FirebaseCore
import FirebaseInstallations
import FirebaseMessaging
import UserNotifications
import ComposeApp

class AppDelegate: UIResponder, UIApplicationDelegate {

    func application(
        _ application: UIApplication,
        configurationForConnecting connectingSceneSession: UISceneSession,
        options: UIScene.ConnectionOptions
    ) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func onAuth() {
        FirebaseApp.configure()
        Messaging.messaging().delegate = self

        UNUserNotificationCenter.current().delegate = self
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]

        UNUserNotificationCenter.current().requestAuthorization(options: authOptions) {
            (granted, error) in guard granted else { return }
            Messaging.messaging().isAutoInitEnabled = true

            DispatchQueue.main.async {
                UIApplication.shared.self.registerForRemoteNotifications()
            }
        }
    }
}

extension AppDelegate: UNUserNotificationCenterDelegate {

    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Firebase registration error")
    }

    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification) async
        -> UNNotificationPresentationOptions {
        let userInfo = notification.request.content.userInfo
        print(userInfo)
        if #available(iOS 14.0, *) {
            return [.list, .banner, .sound]
        } else {
            return [.alert, .sound]
        }
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse
    ) async {
        let userInfo = response.notification.request.content.userInfo
        print(userInfo)
    }
}

extension AppDelegate: MessagingDelegate {
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard let fcmToken else { return }
        print("Firebase registration token: \(fcmToken)")
        Installations.installations().installationID { id, error in
            if let error = error {
                let deviceId = UIDevice.current.identifierForVendor?.uuidString
                guard let deviceId else { return }
                IosFirebaseUtilKt.registerFirebaseToken(deviceId: deviceId, firebaseToken: fcmToken)
                print("Ошибка при получении Installation ID: \(error)")
            } else if let id = id {
                IosFirebaseUtilKt.registerFirebaseToken(deviceId: id, firebaseToken: fcmToken)
            }
        }
    }
}

class FirebaseRegistrationTrigger: FirebaseRegistrationTriggerContract {
    var delegate: AppDelegate?

    init(delegate: AppDelegate) {
        self.delegate = delegate
    }

    func trigger() {
        delegate?.onAuth()
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
    
    init() {
        let trigger = FirebaseRegistrationTrigger(delegate: delegate)
        IosFirebaseUtilKt.setRegistrationTrigger(trigger: trigger)
    }
}
