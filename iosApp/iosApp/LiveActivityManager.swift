import ActivityKit
import Foundation

@objc class LiveActivityManager: NSObject {
    @objc static let shared = LiveActivityManager()
    private var currentActivity: Any? = nil

    @objc func startLiveActivity(steps: Int, goal: Int, calories: Int) {
        if #available(iOS 16.1, *) {
            guard ActivityAuthorizationInfo().areActivitiesEnabled else { return }

            let attributes = StepTrackerAttributes(profileName: "User")
            let initialState = StepTrackerAttributes.ContentState(currentSteps: steps, dailyGoal: goal, calories: calories)

            do {
                let activity = try Activity.request(attributes: attributes, content: .init(state: initialState, staleDate: nil))
                currentActivity = activity
                print("Requested Live Activity \(activity.id)")
            } catch {
                print("Error requesting Live Activity: \(error.localizedDescription)")
            }
        }
    }

    @objc func updateLiveActivity(steps: Int, goal: Int, calories: Int) {
        if #available(iOS 16.1, *) {
            guard let activity = currentActivity as? Activity<StepTrackerAttributes> else { return }
            let updatedState = StepTrackerAttributes.ContentState(currentSteps: steps, dailyGoal: goal, calories: calories)
            
            Task {
                await activity.update(ActivityContent<StepTrackerAttributes.ContentState>(state: updatedState, staleDate: nil))
            }
        }
    }

    @objc func endLiveActivity() {
        if #available(iOS 16.1, *) {
            guard let activity = currentActivity as? Activity<StepTrackerAttributes> else { return }
            Task {
                await activity.end(nil, dismissalPolicy: .immediate)
                currentActivity = nil
            }
        }
    }
}
