import ActivityKit
import WidgetKit
import SwiftUI

struct StepTrackerWidget: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: StepTrackerAttributes.self) { context in
            // Lock Screen UI
            VStack {
                HStack {
                    Image(systemName: "figure.walk")
                        .foregroundColor(.blue)
                    Text("SmartStep Progress")
                        .font(.headline)
                    Spacer()
                    Text("\(context.state.currentSteps) / \(context.state.dailyGoal)")
                        .font(.subheadline)
                }
                ProgressView(value: Double(context.state.currentSteps), total: Double(context.state.dailyGoal))
                    .tint(.blue)
                HStack {
                    Text("\(context.state.calories) kcal")
                        .font(.caption)
                    Spacer()
                }
            }
            .padding()
        } dynamicIsland: { context in
            DynamicIsland {
                // Expanded UI
                DynamicIslandExpandedRegion(.leading) {
                    HStack {
                        Image(systemName: "figure.walk")
                        Text("\(context.state.currentSteps)")
                    }
                }
                DynamicIslandExpandedRegion(.trailing) {
                    Text("\(context.state.dailyGoal) goal")
                }
                DynamicIslandExpandedRegion(.bottom) {
                    ProgressView(value: Double(context.state.currentSteps), total: Double(context.state.dailyGoal))
                        .tint(.blue)
                }
            } compactLeading: {
                Image(systemName: "figure.walk")
                    .foregroundColor(.blue)
            } compactTrailing: {
                Text("\(context.state.currentSteps)")
                    .minimumScaleFactor(0.5)
            } minimal: {
                Image(systemName: "figure.walk")
                    .foregroundColor(.blue)
            }
        }
    }
}
