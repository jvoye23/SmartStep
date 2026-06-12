import ActivityKit
import Foundation

struct StepTrackerAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        var currentSteps: Int
        var dailyGoal: Int
        var calories: Int
    }

    var profileName: String
}
